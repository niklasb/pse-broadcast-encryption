package cryptocast.comm;

/**
 * Wrapper around an instance of {@link OutChannel} that counts outgoing bytes.
 */
public class StatisticalOutChannel implements OutChannel {
    /**
     * Initializes the proxy
     * @param inner The wrapped channel
     */
    public StatisticalOutChannel(OutChannel inner) { }

    /**
     * Sends the given data.
     * @param data The data to send
     */
    public void send(byte[] data) {}

    /**
     * @return The number of sent bytes
     */
    public int getSentBytes() {return 0;}
}
