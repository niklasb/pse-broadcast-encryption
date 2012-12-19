package cryptocast.server;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.SortedMap;
import java.util.TreeMap;

import cryptocast.util.InteractiveCommandLineInterface;

/**
 * Implements the user interface of the server as an interactive console application.
 * @param <ID> The type of the user identities.
 */
public class Shell<ID> extends InteractiveCommandLineInterface {
    private static ShellCommand commands[] = new ShellCommand[] {
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
    };

    private static SortedMap<String, ShellCommand> commandsByName = 
            new TreeMap<String, ShellCommand>();
    static {
        for (ShellCommand cmd : commands) {
            commandsByName.put(cmd.getName(), cmd);
        }
    }

    private Controller<ID> control;

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
        if (cmd.getName() == "help") { help(cmd, args); }
    }

    /**
     * Prints all commands this shell can perform with information about how to use them.
     */
    private void help(ShellCommand cmd, String[] args) throws CommandError {
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
    
    private void commandSyntaxError(ShellCommand cmd) throws CommandError {
        error("Invalid Syntax! Usage: %s %s", cmd.getName(), cmd.getSyntax());
    }
}
