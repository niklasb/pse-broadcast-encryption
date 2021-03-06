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
     * Receives data. Will read exactly the given number of bytes. Only if the
     * end-of-file is reached, it might be that we return less bytes.
     *
     * @param in The stream of data which comes via network.
     * @param buffer The target buffer.
     * @param offset The start offset in array buffer at which the data is written.
     * @param len The maximum number of bytes to read.
     * @return The number of bytes actually read is returned as an integer.
     * @throws IOException thrown when interrupted during busy waiting
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

    /**
     * Directs the data from input stream into output stream.
     * The operation is interruptable only if neither input nor output block!
     *
     * @param in The input stream.
     * @param out The output steam.
     * @param bufsize The length of buffer array.
     * @throws IOException
     */
    public static void copyInterruptable(InputStream in, OutputStream out, int bufsize)
            throws IOException, InterruptedException {
        byte[] buffer = new byte[bufsize];
        int received;
        while ((received = in.read(buffer)) >= 0) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            out.write(buffer, 0, received);
        }
    }
}
