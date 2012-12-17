package cryptocast.comm;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Wrapper around an instance of {@link InChannel} that counts incoming bytes.
 */
public class StatisticalInChannel extends InChannel {
    private int receivedBytes = 0;
    private InChannel inner;
    
    /**
     * Initializes the proxy.
     * @param inner The wrapped channel
     */
    public StatisticalInChannel(InChannel inner) {
        this.inner = inner;
    }

    /**
     * Receives data.
     * @param buffer The target buffer
     */
    @Override
    public int recv(ByteBuffer buffer) throws IOException {
        int received = inner.recv(buffer);
        receivedBytes += received;
        return received;
    }

    /**
     * @return The number of received bytes
     */
    public int getReceivedBytes() {
        return receivedBytes;
    }
}
