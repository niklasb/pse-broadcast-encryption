package cryptocast.comm;

/**
 * Wrapper around an InChannel that counts incoming bytes
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
     * @return the received data
     */
    public ByteBuffer recv(int size) {return null;}

    /**
     * @return the number of received bytes
     */
    public int getReceivedBytes() {return 0;}
}
