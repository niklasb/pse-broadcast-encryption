package cryptocast.crypto;

/**
 * An exception which is thrown when an error occurs while decrypting a cipher
 * text.
 */
public class DecryptionError extends Exception {
    private static final long serialVersionUID = -979360017405928569L;

    /**
     * Creates a new decyption error with the given message.
     *
     * @param msg The error message.
     */
    public DecryptionError(String msg) {
        super(msg);
    }
}
