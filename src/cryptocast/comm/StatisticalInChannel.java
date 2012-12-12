package cryptocast.comm;

/**
 * Wrapper around an instance of {@link InChannel} that counts incoming bytes.
 */
public class StatisticalInChannel implements InChannel {
    /**
     * Initializes the proxy.
     * @param inner The wrapped channel
     */
    public StatisticalInChannel(InChannel inner) { }

    /**
     * Receives data.
     * @param size Maximum amount of bytes to read
     * @param buffer The target buffer
     */
    public void recv(int size, byte[] buffer) {}

    /**
     * @return The number of received bytes
     */
    public int getReceivedBytes() {return 0;}
}
