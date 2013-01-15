package cryptocast.comm;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A filter output stream which limit the number of written bytes per second.
 */
public class ThrottledOutputStream extends FilterOutputStream {
    private long maxBytesPerSec;
    private long bytes = 0;
    private long start;
    
    /**
     * Creates a new instance of ThrottledOutputStream with the given parameter.
     *
     * @param out The output stream.
     * @param maxBytesPerSec Maximum bytes per second.
     */
    public ThrottledOutputStream(OutputStream out, long maxBytesPerSec) {
        super(out);
        this.maxBytesPerSec = maxBytesPerSec;
        start = System.currentTimeMillis();
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte) b }, 0, 1);
    }
    
    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        bytes += len;
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed == 0) {
            out.write(data, offset, len);
            return;
        }
        long bps = bytes * 1000L / elapsed;
        if (bps > maxBytesPerSec) {
            // Oops, sending too fast.
            long wakeElapsed = bytes * 1000L / maxBytesPerSec;
            try {
                Thread.sleep(wakeElapsed - elapsed);
            }
            catch (InterruptedException ignore) { }
        }
        out.write(data, offset, len);
    }
}
