package cryptocast.comm;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A byte-based communication channel from which data can be received.
 */
public abstract class InChannel {

    /**
     * Receives data. Can read less than the remaining number of bytes.
     * 
     * @param buffer The target buffer
     * @return The amount of bytes read
     */
    public abstract int recv(ByteBuffer buffer) throws IOException;

    private static final int POLL_INTERVAL = 50; // milliseconds;

    /**
     * Receives data. Will read exactly the remaining number of bytes;
     * 
     * @param size The exact amount of bytes to read
     * @param buffer The target buffer
     */
    void recvall(ByteBuffer buffer) throws InterruptedException, IOException {
        // use polling to read all data
        int left = buffer.remaining();
        for (;;) {
            int received = recv(buffer);
            left -= received;
            if (left == 0) {
                return;
            }
            Thread.sleep(POLL_INTERVAL);
        }
    }
}
