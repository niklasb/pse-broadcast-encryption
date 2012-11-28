package broadcastenc.server;

import java.io.PrintStream;

import broadcastenc.util.CommandLineInterface;

/** Gets the arguments from the command line and deals with illegal input.
 */
public class Shell extends CommandLineInterface {
	
	private String prompt;
	
	/**
	 * Creates a new Shell object with the given parameters.
	 * @param out Stream to write usual output to.
	 * @param err Stream to write error messages to.
	 * @param prompt 
	 */
	public Shell(PrintStream out, PrintStream err, String prompt) {
		super(out, err);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void start(String[] args) throws Exit {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Prints a String to the out-stream and waits for input in form of a String from the in-stream.
	 * @param print Message to be printed
	 * @return the String from the in-stream
	 */
	private String askString(String print) {
		
	}
    
}