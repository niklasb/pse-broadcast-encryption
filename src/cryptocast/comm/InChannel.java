package cryptocast.comm;

/**
 * A byte-based communication channel from which data can be received.
 */
public interface InChannel {
    /**
     * Receives data.
     * @param size maximum amount of bytes to read
     * @param buffer The target buffer
     */
    public void recv(int size, byte[] buffer);
}
