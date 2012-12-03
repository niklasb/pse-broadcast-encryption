package cryptocast.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * A framework class to implement interactive command-line interfaces. The class implements
 * a read-parse-execute main loop and provides hooks for subclasses to implement the missing
 * functionality.
 */
public abstract class InteractiveCommandLineInterface extends CommandLineInterface {
    /** An error within one of the commands. Will be caught by the main loop */
    protected class CommandError extends Throwable {
        public String msg;
        /** Initializes the error
        * @param msg The error message
        */
        public CommandError(String msg) {
            this.msg = msg;
        }
        /** @return The associated error message */
        public String getMessage() { return msg; }
    }

    /** {@inheritDoc} */
    public InteractiveCommandLineInterface(InputStream in, PrintStream out, PrintStream err) {
        super(in, out, err);
    }

    /** {@inheritDoc} */
    @Override
    protected void start(String[] args) throws CommandLineInterface.Exit {
        mainloop();
    }

    /** Starts the interactive Prompt-Read-Evaluate main loop.  */
    protected void mainloop() throws CommandLineInterface.Exit {
        for (;;) {
            print(getPrompt());
            String raw = null;
            try {
                raw = in.readLine();
            } catch (IOException e) {
                fatalError("Error reading a line of input: %s", e.getMessage());
            }
            if (raw == null) { // handle ^D/^Z (POSIX/Win) gracefully
                println("");
                exit(0);
            }

            String[] cmdline = raw.trim().split("\\s+");
            String cmd = cmdline[0];
            String[] args = new String[cmdline.length - 1];
            System.arraycopy(cmdline, 1, args, 0, cmdline.length - 1);
            try {
                performCommand(cmd, args);
            } catch (CommandError e) {
                err.println(String.format(getErrorFormat(), e.getMessage()));
            }
        }
    }

    /**
     * Executes the given command with the given arguments. Must be implemented by subclasses.
     * @param cmd The command name
     * @param args The command arguments
     */
    protected abstract void performCommand(String cmd, String[] args) throws CommandError;

    /**
     * Helper function to trigger an error withing a command's execution and break out to
     * the main loop.
     */
    protected void error(String format, Object... args) throws CommandError {
        throw new CommandError(String.format(format, args));
    }

    /** @return The input prompt */
    protected String getPrompt() { return "> "; }
}
