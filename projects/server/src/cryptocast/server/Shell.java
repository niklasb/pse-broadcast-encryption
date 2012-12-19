package cryptocast.server;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.SortedMap;

import cryptocast.util.InteractiveCommandLineInterface;

/**
 * Implements the user interface of the server as an interactive console application.
 * @param <ID> The type of the user identities.
 */
public class Shell<ID> extends InteractiveCommandLineInterface {
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
    protected void performCommand(String cmdStr, String[] args) throws CommandError {
        ShellCommand cmd = ShellCommand.commands.get(cmdStr);
        if (cmd == null) {
            error("No such command! Type `help' to get an overview of the available commands.");
        }
        switch (cmd) {
        case HELP: help(); break;
        }
    }

    /**
     * Prints all commands this shell can perform with information about how to use them.
     */
    private void help() {
        println("Available commands:");
        println();
        for (ShellCommand cmd : ShellCommand.commands.values()) {
            printf("%-12s %s\n", cmd.getName(), cmd.getShortDesc());
        }
    }
}
