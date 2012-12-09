package cryptocast.comm;

import java.nio.ByteBuffer;

/**
 * Wrapper around an OutChannel that counts outgoing bytes
 */
public class StatisticalOutChannel implements OutChannel {
    /**
     * Outitializes the proxy
     * @param inner the wrapped channel
     */
    public StatisticalOutChannel(OutChannel inner) { }

    /**
     * Sends the given data.
     * @param data the data to send
     */
    public void send(ByteBuffer data) {}

    /**
     * @return the number of sent bytes
     */
    public int getSentBytes() {return 0;}
}
