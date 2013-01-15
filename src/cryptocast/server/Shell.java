package cryptocast.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import cryptocast.crypto.NoMoreRevocationsPossibleError;
import cryptocast.crypto.naorpinkas.NPIdentity;
import cryptocast.util.InteractiveCommandLineInterface;
import cryptocast.util.ByteUtils;
import static cryptocast.util.ErrorUtils.cannotHappen;
import static cryptocast.util.FileUtils.expandPath;

/**
 * Implements the user interface of the server as an interactive console application.
 */
public class Shell extends InteractiveCommandLineInterface {
    private static final Logger log = LoggerFactory.getLogger(Shell.class);
    
    private static ShellCommand commands[] = {
        new ShellCommand("help",
                         "[<command>]",
                         "Show the command line help"),
        new ShellCommand("add",
                         "<name>",
                         "Add a new user to the group of recipients"),
        new ShellCommand("revoke",
                         "[<name>, [<name>, ... ]]",
                         "Revoke users"),
        new ShellCommand("unrevoke",
                         "<name>",
                         "Unrevoke a user"),
        new ShellCommand("users",
                         "",
                         "List users"),
        new ShellCommand("save-keys",
                         "dir [<user>, [<user>, ...]]",
                         "Save user keys to a directory"),
        new ShellCommand("stream-stdin",
                         "",
                         "Captures input from STDIN and broadcasts it"),
        new ShellCommand("stream-sample-text",
                         "",
                         "Stream an infinite stream of sample text"),
        new ShellCommand("stream-mp3",
                         "<file>",
                         "Stream an MPEG-3 audio file"),
        new ShellCommand("init",
                         "<t>",
                         "Create a whole new crypto context"),
        new ShellCommand("stop-stream",
                         "",
                         "Stops the current stream"),
    };

    private static SortedMap<String, ShellCommand> commandsByName = 
            new TreeMap<String, ShellCommand>();
    static {
        for (ShellCommand cmd : commands) {
            commandsByName.put(cmd.getName(), cmd);
        }
    }

    private Controller control;

    /**
     * Creates a new Shell object with the given parameters.
     * 
     * @param in The input stream
     * @param out Stream to write normal output to.
     * @param err Stream to write error messages to.
     */
    public Shell(BufferedReader in, PrintStream out, PrintStream err, 
            Controller control) {
        super(in, out, err);
        this.control = control;
    }

    @Override
    protected void start() throws Exit {
        println("Hello from the CryptoCast server. Enjoy.");
        println();
        printf("User database: %s\n", control.getDatabaseFile());
        printf("Listening on %s\n", control.getListenAddress());
        super.start();
    }
    
    @Override
    protected void performCommand(String cmdName, String[] args) 
            throws CommandError, Exit {
        ShellCommand cmd = commandsByName.get(cmdName);
        if (cmd == null) {
            error("No such command! Type `help' to get an overview of the available commands.");
        }
        
        Method method = null;
        try {
            method = getClass().getDeclaredMethod(
                    "cmd" + capitalizeCmdName(cmd.getName()), 
                    ShellCommand.class, String[].class);
        } catch (NoSuchMethodException e) {
            cannotHappen(e);
        }
        try {
            method.invoke(this, cmd, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof CommandError) {
                throw (CommandError) e.getCause();
            } else if (e.getCause() instanceof Exit) {
                throw (Exit) e.getCause();
            }
            cannotHappen(e);
        } catch (Exception e) {
            log.error("Could not execute command", e);
        }
    }
    
    private String capitalizeCmdName(String name) {
        String[] parts = name.split("-");
        String result = "";
        for (String p : parts) {
            result += p.substring(0, 1).toUpperCase() + p.substring(1);
        }
        return result;
    }
    
    /**
     * Prints all commands this shell can perform with information about how to use them.
     */
    protected void cmdHelp(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length > 1) {
            commandSyntaxError(cmd);
        }
        if (args.length == 1) {
            ShellCommand helpCmd = commandsByName.get(args[0]);
            if (helpCmd == null) {
                error("No such command: `%s'", args[0]);
            }
            printf("Usage: %s %s\n", helpCmd.getName(), helpCmd.getSyntax());
            println();
            println(helpCmd.getLongDesc());
        } else {
            println("Available commands:");
            println();
            for (ShellCommand c : commandsByName.values()) {
                printf("%-12s %s\n", c.getName(), c.getShortDesc());
            }
            println();
            println("Use `help <cmd>' to get more information about a specific command");
        }
    }
    
    protected void cmdInit(ShellCommand cmd, String[] args) throws CommandError, Exit {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }
        Optional<Integer> mT = parseInt(args[0]);
        if (!mT.isPresent() || mT.get().intValue() < 0) {
            error("Invalid integer value: " + args[0]);
        }
        int t = mT.get().intValue();
        try {
            control.reinitializeCrypto(t);
        } catch (Exception e) {
            fatalError(e);
        }
    }

    protected void cmdUsers(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 0) {
            commandSyntaxError(cmd);
        }

        Set<User<NPIdentity>> users = getModel().getUsers();
        int revoked = 0;
        for (User<NPIdentity> user : users) {
            if (getModel().isRevoked(user)) {
                revoked++;
            }
        }
        printf("Have %d users, %d/%d revoked:\n", users.size(), revoked, control.getT());
        for (User<NPIdentity> user : users) {
            printf("  %-20s%s\n", user.getName(), 
                          getModel().isRevoked(user) ? " revoked" : "");
        }
    }
    
    protected void cmdAdd(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }

        Optional<User<NPIdentity>> mUser = getModel().createNewUser(args[0]);
        if (!mUser.isPresent()) {
            error("User with this name already existing!");
        }
    }
    
    protected void cmdRevoke(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length < 1) {
            commandSyntaxError(cmd);
        }
        
        ImmutableSet.Builder<User<NPIdentity>> users = ImmutableSet.builder();
        for (String name : args) {
            users.add(getUser(name));
        }
        try {
            getModel().revoke(users.build()); 
        } catch (NoMoreRevocationsPossibleError e) {
            error("Cannot revoke that many users!");
        }
    }
    
    protected void cmdUnrevoke(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }

        User<NPIdentity> user = getUser(args[0]);
        if (!getModel().unrevoke(user)) {
            error("User already authorized!");
        }
    }

    protected void cmdSaveKeys(ShellCommand cmd, String[] args) throws CommandError, Exit {
        if (args.length < 1) {
            commandSyntaxError(cmd);
        }

        File dir = expandPath(args[0]);
        if (!dir.isDirectory()) {
            error("Target directory does not exist!");
        }
        Set<User<NPIdentity>> users;
        if (args.length > 1) {
            users = Sets.newHashSet();
            for (int i = 1; i < args.length; ++i) {
                users.add(getUser(args[i]));
            }
        } else {
            users = getModel().getUsers();
        }
        try {
            control.saveUserKeys(dir, users);
        } catch (Exception e) {
            fatalError(e);
        }
    }
    
    protected void cmdStreamSampleText(ShellCommand cmd, String[] args) throws CommandError, Exit {
        if (args.length != 0) {
            commandSyntaxError(cmd);
        }
        try {
            control.streamSampleText();
        } catch (Exception e) {
            fatalError(e);
        }
    }
    
    protected void cmdStreamStdin(ShellCommand cmd, String[] args) 
                                throws CommandError, Exit {
        if (args.length != 0) {
            commandSyntaxError(cmd);
        }
        PipedOutputStream pipeOut = null;
        PipedInputStream pipeIn = null;
        try {
            pipeOut = new PipedOutputStream();
            pipeIn = new PipedInputStream(pipeOut);
        } catch (Exception e) {
            fatalError(e);
        }
        final BufferedReader in = this.in;
        final OutputStream out = pipeOut;
        Thread reader = new Thread(new Runnable() {
            public void run() {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.write(ByteUtils.encodeUtf8(line + "\n"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        reader.start();
        try {
            control.stream(pipeIn, 0x1000);
        } catch (Exception e) {
            fatalError(e);
        }
    }
    
    protected void cmdStreamMp3(ShellCommand cmd, String[] args) throws CommandError, Exit {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }
        File file = expandPath(args[0]);
        try {
            control.streamAudio(file);
        } catch (Exception e) {
            fatalError(e);
        }
    }
    
    protected void cmdStopStream(ShellCommand cmd, String[] args) throws CommandError, Exit {
        if (args.length > 0) {
            commandSyntaxError(cmd);
        }
        try {
            control.stopStreamThread();
        } catch (Exception e) {
            fatalError(e);
        }
    }

    private User<NPIdentity> getUser(String name) throws CommandError {
        Optional<User<NPIdentity>> mUser = getModel().getUserByName(name);
        if (!mUser.isPresent()) {
            error("No such user: " + name);
        }
        return mUser.get();
    }

    private ServerData<NPIdentity> getModel() {
        return control.getModel();
    }
    
    private void commandSyntaxError(ShellCommand cmd) throws CommandError {
        error("Invalid Syntax! Usage: %s %s", cmd.getName(), cmd.getSyntax());
    }
    
    /**
     * Parses the string.
     * 
     * @param str The string to be parsed.
     * @return An integer.
     */
    public static Optional<Integer> parseInt(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Optional.absent();
        }
    }
}