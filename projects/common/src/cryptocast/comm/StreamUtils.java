package cryptocast.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public static int readall(InputStream in, byte[] buffer, int offset, int len) 
                                  throws IOException {
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
            try {
                Thread.sleep(POLL_INTERVAL);
            } catch (InterruptedException e) {
                throw new IOException("Interrupted during busy waiting", e);
            }
        }
    }
    
    public static void shovel(InputStream in, OutputStream out) 
            throws IOException {
        byte[] tmp = new byte[4096];
        int received;
        while ((received = in.read(tmp)) >= 0) {
            System.out.println("received: " + received);
            out.write(tmp, 0, received);
        }
    }
}
