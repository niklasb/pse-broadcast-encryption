package cryptocast.server;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Optional;

import cryptocast.crypto.NoMoreRevocationsPossibleError;
import cryptocast.crypto.naorpinkas.NaorPinkasIdentity;
import cryptocast.util.InteractiveCommandLineInterface;
import static cryptocast.util.ErrorUtils.cannotHappen;

/**
 * Implements the user interface of the server as an interactive console application.
 */
public class Shell extends InteractiveCommandLineInterface {
    private static ShellCommand commands[] = {
        new ShellCommand("help",
                         "[<command>]",
                         "Shows the command line help",
                         null),
        new ShellCommand("add",
                         "<name>",
                         "Adds a new user to the group of recipients",
                         null),
        new ShellCommand("revoke",
                         "<name>",
                         "Revokes a user",
                         null),
        new ShellCommand("unrevoke",
                         "<name>",
                         "Unrevokes a user",
                         null),
        new ShellCommand("users",
                         "",
                         "Lists users",
                         null),
        new ShellCommand("save",
                         "",
                         "Saves the current crypto context and users",
                         null),
        new ShellCommand("save-keys",
                         "dir [<user>, [<user>, ...]]",
                         "Saves user keys to a directory",
                         null),
        new ShellCommand("init",
                         "<t>",
                         "Creates a whole new crypto context",
                         null),
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
     * @param in The input stream
     * @param out Stream to write normal output to.
     * @param err Stream to write error messages to.
     */
    public Shell(InputStream in, PrintStream out, PrintStream err) {
        super(in, out, err);
    }

    @Override
    protected void performCommand(String cmdName, String[] args) 
            throws CommandError, Exit {
        ShellCommand cmd = commandsByName.get(cmdName);
        if (cmd == null) {
            error("No such command! Type `help' to get an overview of the available commands.");
        }
        if (cmd.getName() == "help") { cmdHelp(cmd, args); }
        else if (cmd.getName() == "save") { cmdSave(cmd, args); }
        else if (cmd.getName() == "init") { cmdInit(cmd, args); }
        else if (cmd.getName() == "users") { cmdUsers(cmd, args); }
        else if (cmd.getName() == "add") { cmdAddUser(cmd, args); }
        else if (cmd.getName() == "revoke") { cmdRevokeUser(cmd, args); }
        else if (cmd.getName() == "unrevoke") { cmdUnrevokeUser(cmd, args); }
        else if (cmd.getName() == "save-keys") { cmdSaveKeys(cmd, args); }
        else { cannotHappen(); }
    }

    @Override
    protected String getBasicUsage() {
        return "cryptocast-server database-file [listen-host:]listen-port";
    }

    @Override
    protected void parseArgs(String[] args) throws Exit {
        if (args.length != 2) {
            usage();
            exit(2);
        }
        File db = new File(args[0]);
        String[] listen = args[1].split(":");
        String host, sPort;
        int port  = -1;
        if (listen.length < 2) {
            host = "127.0.0.1";
            sPort = listen[0];
        } else {
            host = listen[0];
            sPort = listen[1];
        }
        try {
            port = Integer.parseInt(sPort);
        } catch (NumberFormatException e) {
            usage();
            exit(2);
        }
        try {
            control = Controller.start(db, new InetSocketAddress(host, port));
        } catch (Exception e) {
            fatalError(e);
        }
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

    private void cmdSave(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 0) {
            commandSyntaxError(cmd);
        }
        try {
            control.saveDatabase();
        } catch (Exception e) {
            error("Cannot save database: " + e.getMessage());
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
    
    private void cmdAddUser(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }

        Optional<User<NaorPinkasIdentity>> mUser = getModel().createNewUser(args[0]);
        if (!mUser.isPresent()) {
            error("User with this name already existing!");
        }
        println("User was added successfully");
    }
    
    private void cmdRevokeUser(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }

        User<NaorPinkasIdentity> user = getUser(args[0]);
        try {
            if (!getModel().revoke(user)) {
                error("User already revoked!");
            }
        } catch (NoMoreRevocationsPossibleError e) {
            error("Cannot revoke any more users!");
        }
        println("User was revoked successfully");
    }
    
    private void cmdUnrevokeUser(ShellCommand cmd, String[] args) throws CommandError {
        if (args.length != 1) {
            commandSyntaxError(cmd);
        }

        User<NaorPinkasIdentity> user = getUser(args[0]);
        if (!getModel().unrevoke(user)) {
            error("User already authorized!");
        }
        println("User was authorized successfully");
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
    
    private User<NaorPinkasIdentity> getUser(String name) throws CommandError {
        Optional<User<NaorPinkasIdentity>> mUser = getModel().getUserByName(name);
        if (!mUser.isPresent()) {
            error("No such user: " + name);
        }
        return mUser.get();
    }
    
    private ServerData<NaorPinkasIdentity> getModel() {
        return control.getModel();
    }
    
    private void commandSyntaxError(ShellCommand cmd) throws CommandError {
        error("Invalid Syntax! Usage: %s %s", cmd.getName(), cmd.getSyntax());
    }
    
    private File expandPath(String path) {
        if (path.startsWith("~" + File.separator)) {
            path = System.getProperty("user.home") + path.substring(1);
        }
        return new File(path);
    }
    
    private Optional<Integer> parseInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.absent();
        }
    }
}
