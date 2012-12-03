package broadcastenc.util;

import java.io.PrintStream;

/** Common base class for command line programs. */
public abstract class CommandLineInterface {
	
    /** Signals the exit of the application. */
    protected class Exit extends Throwable {
        private int status;
        /** Initializes a new Exit instance
         * @param status The exit code
         */
        public Exit(int status) { this.status = status; }

        /** @return the exit code */
        public int getStatus() { return status; }

        private static final long serialVersionUID = 1;
    }

    // the program's output streams
    protected PrintStream out;
    protected PrintStream err;

    /** Initializes a new Cli instance
     * @param out The stream for program output
     * @param err The stream for error output
     */
    public CommandLineInterface(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    /** Runs the application.
     * @param args The command line arguments
     * @return The exit code
     */
    public int run(String[] args) {
        try {
            start(args);
            return 0;
        } catch (Exit e) {
            return e.getStatus();
        }
    }

    /** The main program logic (must be overridden by subclasses)
     * @param args The command line arguments
     * @throws Exit if the program finishes early
     */
    protected abstract void start(String[] args) throws Exit;

    /** Prints an error and exits
     * @param msg The error message
     * @throws Exit with an exit code of 2
     */
    protected void error(String msg) throws Exit {
        err.println(msg);
        exit(2);
    }

    /** Exits the application
     * @param status The exit code
     * @throws Exit with the given exit code
     */
    protected void exit(int status) throws Exit {
        throw new Exit(status);
    }

    /** Prints usage information */
    protected void usage() {
        err.println("Usage: " + getBasicUsage());
        printAdditionalUsage();
    }

    /** @return basic usage information for the program (should be overridden) */
    protected String getBasicUsage() {
        return "java [program] [arguments]";
    }

    /** Prints additional usage information (may be overridden) */
    protected void printAdditionalUsage() { }
}
