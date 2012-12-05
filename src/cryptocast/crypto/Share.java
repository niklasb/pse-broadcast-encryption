package cryptocast.crypto;

/**
 * Represents a possibly incomplete piece of information to restore a secret value.
 * @param <S> the type of secret.
 */
public interface Share<S> {
    /**
     * An exception that is thrown when a secret cannot be restored because
     * of missing information.
     */
    class InsufficientInformationException extends Exception {}

    /**
     * @return whether the share is complete and can be used to restore
     * the value.
     */
    public boolean isComplete();
    /**
     * Restore the value represented by this share.
     * @throws InsufficientInformationException if the share is incomplete
     * @return The restored value
     */
    public S restore() throws InsufficientInformationException;
    /**
     * @return A binary representation of the share
     */
    public byte[] pack();
}

