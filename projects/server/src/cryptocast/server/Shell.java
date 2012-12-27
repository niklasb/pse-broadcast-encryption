package cryptocast.server;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Throwables;

import cryptocast.util.CommandLineInterface;
import cryptocast.util.InteractiveCommandLineInterface;

/**
 * Implements the user interface of the server as an interactive console application.
 */
public class Shell extends InteractiveCommandLineInterface {
    private static ShellCommand commands[] = {
        new ShellCommand("help",
                         "[<command>]",
                         "Shows the command line help",
                         null),
        new ShellCommand("addUser",
                         "<name>",
                         "Adds a new user to the group of recipients",
                         null),
        new ShellCommand("revokeUser",
                         "<name>",
                         "Revokes a user",
                         null),
        new ShellCommand("save",
                         "",
                         "Saves the current user database",
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
    protected void performCommand(String cmdName, String[] args) throws CommandError {
        ShellCommand cmd = commandsByName.get(cmdName);
        if (cmd == null) {
            error("No such command! Type `help' to get an overview of the available commands.");
        }
        if (cmd.getName() == "help") { cmdHelp(cmd, args); }
        if (cmd.getName() == "save") { cmdSave(); }
    }
    
    @Override
    protected String getBasicUsage() {
        return "cryptocast-server database-file [listen-host:]listen-port";
    }

    @Override
    protected void parseArgs(String[] args) throws CommandLineInterface.Exit {
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

    private void cmdSave() throws CommandError {
        try {
            control.saveDatabase();
        } catch (Exception e) {
            error("Cannot save database:\n" + Throwables.getStackTraceAsString(e));
        }
    }

    private void commandSyntaxError(ShellCommand cmd) throws CommandError {
        error("Invalid Syntax! Usage: %s %s", cmd.getName(), cmd.getSyntax());
    }
}
