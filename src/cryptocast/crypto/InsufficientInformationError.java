package cryptocast.crypto;

/**
 * An exception which is thrown when insufficient information is provided for the Naor-Pinkas Client.
 */
public class InsufficientInformationError extends DecryptionError {
    private static final long serialVersionUID = 297331299012388587L;
    
    /**
     * Creates a new insufficient error with the given message.
     * 
     * @param msg The error message.
     */
    public InsufficientInformationError(String msg) {
        super(msg);
    }
}
