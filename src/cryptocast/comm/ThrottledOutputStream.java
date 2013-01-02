package cryptocast.comm;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ThrottledOutputStream extends FilterOutputStream {
    private long maxBps;
    private long bytes = 0;
    private long start;
    
    public ThrottledOutputStream(OutputStream out, long maxBps) {
        super(out);
        this.maxBps = maxBps;
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
        long bps = bytes * 1000L / elapsed;
        if (bps > maxBps) {
            // Oops, sending too fast.
            long wakeElapsed = bytes * 1000L / maxBps;
            try {
                Thread.sleep( wakeElapsed - elapsed );
            }
            catch (InterruptedException ignore) { }
        }
        out.write(data, offset, len);
    }
}
