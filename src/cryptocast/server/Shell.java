package cryptocast.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import cryptocast.crypto.NoMoreRevocationsPossibleError;
import cryptocast.crypto.naorpinkas.NaorPinkasIdentity;
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
                         "<name>",
                         "Revoke a user"),
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
    private void cmdHelp(ShellCommand cmd, String[] args) throws CommandError {
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
    
    private void cmdInit(ShellCommand cmd, String[] args) throws CommandError, Exit {
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

    private void cmdUsers(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 0) {
            commandSyntaxError(cmd);
        }

        List<User<NaorPinkasIdentity>> users = getModel().getUsers();
        int revoked = 0;
        for (User<NaorPinkasIdentity> user : users) {
            if (getModel().isRevoked(user)) {
                revoked++;
            }
        }
        printf("Have %d users, %d/%d revoked:\n", users.size(), revoked, control.getT());
        for (User<NaorPinkasIdentity> user : users) {
            printf("  %-20s%s\n", user.getName(), 
                          getModel().isRevoked(user) ? " revoked" : "");
        }
    }
    
    private void cmdAdd(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }

        Optional<User<NaorPinkasIdentity>> mUser = getModel().createNewUser(args[0]);
        if (!mUser.isPresent()) {
            error("User with this name already existing!");
        }
    }
    
    private void cmdRevoke(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length < 1) {
            commandSyntaxError(cmd);
        }
        
        List<User<NaorPinkasIdentity>> existingUsers = getExistingUsers(args);
        //TODO the following would be required by different notifications to the user
        // or different implementation: revoke methode in model nimmt liste.
        //List<User<NaorPinkasIdentity>> nonExistingUsers = getNonExistingUsers(args);
        //List<User<NaorPinkasIdentity>> alreadyRevokedUsers = new ArrayList<User<NaorPinkasIdentity>>();
        //List<User<NaorPinkasIdentity>> newRevokedUsers = new ArrayList<User<NaorPinkasIdentity>>();
        for(User<NaorPinkasIdentity> user : existingUsers) {
            try {
                if (!getModel().revoke(user)) {
                    log.info("User " + user.getName() + " is already revoked!");
                    //alreadyRevokedUsers.add(user);
                } else {
                    log.info("User " + user.getName() + " is now revoked!");
                    //newRevokedUsers.add(user);
                }
            } catch (NoMoreRevocationsPossibleError e) {
                error("Cannot revoke any more users!");
            }
        }
    }
    
    private void cmdUnrevoke(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }

        User<NaorPinkasIdentity> user = getUser(args[0]);
        if (!getModel().unrevoke(user)) {
            error("User already authorized!");
        }
    }

    private void cmdSaveKeys(ShellCommand cmd, String[] args) throws CommandError, Exit {
        if (args.length < 1) {
            commandSyntaxError(cmd);
        }

        File dir = expandPath(args[0]);
        if (!dir.isDirectory()) {
            error("Target directory does not exist!");
        }
        List<User<NaorPinkasIdentity>> users;
        if (args.length > 1) {
            users = new ArrayList<User<NaorPinkasIdentity>>();
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
    
    private void cmdStreamSampleText(ShellCommand cmd, String[] args) throws CommandError, Exit {
        if (args.length != 0) {
            commandSyntaxError(cmd);
        }
        try {
            control.streamSampleText();
        } catch (Exception e) {
            fatalError(e);
        }
    }
    
    private void cmdStreamStdin(ShellCommand cmd, String[] args) 
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
    
    private void cmdStreamMp3(ShellCommand cmd, String[] args) throws CommandError, Exit {
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
    
    private void cmdStopStream(ShellCommand cmd, String[] args) throws CommandError, Exit {
        if (args.length > 0) {
            commandSyntaxError(cmd);
        }
        try {
            control.stopStream();
        } catch (Exception e) {
            fatalError(e);
        }
    }

    private User<NaorPinkasIdentity> getUser(String name) throws CommandError {
        Optional<User<NaorPinkasIdentity>> mUser = getModel().getUserByName(name);
        if (!mUser.isPresent()) {
            error("No such user: " + name);
        }
        return mUser.get();
    }
    
    private List<User<NaorPinkasIdentity>> getExistingUsers(String[] names) {
        List<User<NaorPinkasIdentity>> result = new ArrayList<User<NaorPinkasIdentity>>();
        ServerData<NaorPinkasIdentity> model = getModel();
        Optional<User<NaorPinkasIdentity>> mUser;
        for (String name : names) {
            mUser = model.getUserByName(name);
            if (mUser.isPresent()) {
                result.add(mUser.get());
            } else {
                log.info("User with the name " + name + " does not exist!");
            }
        }
        return result;
    }
     
    private ServerData<NaorPinkasIdentity> getModel() {
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