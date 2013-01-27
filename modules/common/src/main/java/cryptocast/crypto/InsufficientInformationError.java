package cryptocast.crypto;

/**
 * An exception that represents an error due to missing information (for
 * example, in a threshold scheme).
 */
public class InsufficientInformationError extends DecryptionError {
    private static final long serialVersionUID = 297331299012388587L;

    /**
     * Creates a new insufficient error with the given message.
     * @param msg The error message.
     */
    public InsufficientInformationError(String msg) {
        super(msg);
    }
}
