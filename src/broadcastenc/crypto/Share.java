package broadcastenc.crypto;

class InsufficientInformationException extends Exception {}

/**
 * Represents a possibly incomplete piece of information to build a value
 * @param T the type of the partial value
 */
public interface Share<T> {
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
    public T restore() throws InsufficientInformationException;
    /**
     * @return A binary representation of the share
     */
    public byte[] pack();
}

