package cryptocast.server;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

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
    protected void performCommand(String cmd, String[] args) throws CommandError {
    }

    /**
     * Prints all commands this shell can perform with information about how to use them.
     */
    private void help() {
    }
}
