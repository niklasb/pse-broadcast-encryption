package cryptocast.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

/**
 * An {@link InputStream} proxy which limits the number of bytes read per second.
 */
public class ThrottledInputStream extends InputStream {
    private long maxBytesPerSec;
    private long bytes = 0;
    private long start;
    private InputStream in;

    /**
     * Creates a new instance of ThrottledOutputStream with the given parameter.
     *
     * @param in The underlying input stream.
     * @param maxBytesPerSec Maximum number of bytes per second.
     */
    public ThrottledInputStream(InputStream in, long maxBytesPerSec) {
        this.in = in;
        this.maxBytesPerSec = maxBytesPerSec;
        start = System.currentTimeMillis();
    }

    private byte[] onebyte = new byte[1];

    @Override
    public int read() throws IOException {
        int ret = read(onebyte);
        if (ret < 0) { return ret; }
        return onebyte[0];
    }

    @Override
    public int read(byte[] data, int offset, int len) throws IOException {
        int recv = in.read(data, offset, len);
        bytes += recv;
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed == 0) { // to avoid division by zero
            return recv;
        }
        long actualBps = bytes * 1000L / elapsed;
        if (actualBps > maxBytesPerSec) {
            // Oops, receiving too fast.
            long wakeElapsed = bytes * 1000L / maxBytesPerSec;
            try {
                Thread.sleep(wakeElapsed - elapsed);
            }
            catch (InterruptedException e) {
                throw new InterruptedIOException("Interrupted during IO");
            }
        }
        return recv;
    }
}
