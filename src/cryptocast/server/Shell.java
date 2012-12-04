package cryptocast.server;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import cryptocast.util.InteractiveCommandLineInterface;

/**
 * Gets the arguments from the command line and deals with illegal input.
 */
public class Shell extends InteractiveCommandLineInterface {
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
    protected void performCommand(String cmd, String[] args) throws CommandError {
    }

    /**
     * Prints all commands this shell can perform with information about how to use them.
     */
    private void help() {
    }
}
