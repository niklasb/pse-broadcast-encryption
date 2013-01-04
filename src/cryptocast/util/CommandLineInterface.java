package cryptocast.util;

import java.io.BufferedReader;
import java.io.PrintStream;

import com.google.common.base.Throwables;

/**
 * A simple framework for command line programs.
 */
public abstract class CommandLineInterface {
    /**
     * Signals the exit of the application.
     */
    protected class Exit extends Throwable {
        private static final long serialVersionUID = 6306942436754821406L;
        
        private int status;
        
        /**
         * Initializes a new Exit instance.
         * @param status The exit code
         */
        public Exit(int status) { this.status = status; }

        /**
         * @return The exit code
         */
        public int getStatus() { return status; }
    }

    // the program's output streams
    protected BufferedReader in;
    protected PrintStream out;
    protected PrintStream err;

    /**
     * Initializes a new CLI instance.
     * @param in The stream for program input
     * @param out The stream for program output
     * @param err The stream for error output
     */
    public CommandLineInterface(BufferedReader in, PrintStream out, PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

    /**
     * Runs the application.
     * @return The exit code
     */
    public int run() {
        try {
            start();
            return 0;
        } catch (Exit e) {
            return e.getStatus();
        }
    }

    /**
     * The main program logic (must be overridden by subclasses)
     * @param args The command line arguments
     * @throws Exit if the program finishes early
     */
    protected abstract void start() throws Exit;

    /**
     * Prints a string to the output stream.
     * @param format The string to print (printf format string).
     * @param args The printf arguments
     */
    protected void printf(String format, Object... args) { out.printf(format, args); }

    /**
     * Prints a string to the output stream.
     * @param str The string to print.
     */
    protected void print(String str) { out.print(str); }

    /**
     * Prints a string to the output stream after appending a newline.
     * @param str The string to print.
     */
    protected void println(String str) { out.println(str); }
    protected void println() { out.println(); }

    /**
     * @return The string format to use for writing error messages to the
     * screen.
     */
    protected String getErrorFormat() { return "[!] %s"; }

    /**
     * Prints an error and exits.
     * @param format The error message (printf format string)
     * @param args The printf arguments
     * @throws Exit with an exit code of 2
     */
    protected void fatalError(String format, Object... args) throws Exit {
        err.println(String.format(getErrorFormat(), String.format(format, args)));
        exit(1);
    }

    protected void fatalError(Throwable e) throws Exit {
        err.printf(getErrorFormat() + "\n", "Fatal error!");
        err.println(Throwables.getStackTraceAsString(e));
        exit(1);
    }

    /**
     * Exits the application.
     * @param status The exit code
     * @throws Exit with the given exit code.
     */
    protected void exit(int status) throws Exit {
        throw new Exit(status);
    }
}
