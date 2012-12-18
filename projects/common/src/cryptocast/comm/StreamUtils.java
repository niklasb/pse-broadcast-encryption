package cryptocast.comm;

import java.io.IOException;
import java.io.InputStream;

/**
 * A byte-based communication channel from which data can be received.
 */
public class StreamUtils {
    private static final int POLL_INTERVAL = 50; // milliseconds
    
    /**
     * Receives data. Will read exactly the given number of bytes or 
     * less on EOF.
     * 
     * @param size The exact amount of bytes to read
     * @param buffer The target buffer
     */
    static int readall(InputStream in, byte[] buffer, int offset, int len) 
                            throws InterruptedException, IOException {
        // use polling to read all data
        int total = 0;
        for (;;) {
            int received = in.read(buffer, offset, len);
            if (received < 0) {
                return total;
            }
            total += received;
            offset += received;
            len -= received;
            if (len == 0) {
                return total;
            }
            Thread.sleep(POLL_INTERVAL);
        }
    }
}
