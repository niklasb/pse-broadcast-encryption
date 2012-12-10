package cryptocast.comm;

/**
 * Wrapper around an {@link InChannel} that counts incoming bytes
 */
public class StatisticalInChannel implements InChannel {
    /**
     * Initializes the proxy
     * @param inner the wrapped channel
     */
    public StatisticalInChannel(InChannel inner) { }

    /**
     * Receives data.
     * @param size maximum amount of bytes to read
     * @param buffer the target buffer
     */
    public void recv(int size, byte[] buffer) {}

    /**
     * @return the number of received bytes
     */
    public int getReceivedBytes() {return 0;}
}
