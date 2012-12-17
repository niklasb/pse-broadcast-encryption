package cryptocast.comm;

import java.io.IOException;

/**
 * Wrapper around an instance of {@link OutChannel} that counts outgoing bytes.
 */
public class StatisticalOutChannel implements OutChannel {
    private OutChannel inner;
    private int sentBytes = 0;

    /**
     * Initializes the proxy
     * @param inner The wrapped channel
     */
    public StatisticalOutChannel(OutChannel inner) {
        this.inner = inner; 
    }

    /**
     * Sends the given data.
     * @param data The data to send
     */
    @Override
    public void send(byte[] data) throws IOException {
        sentBytes += data.length;
        inner.send(data);
    }

    /**
     * @return The number of sent bytes
     */
    public int getSentBytes() {
        return sentBytes;
    }
}
