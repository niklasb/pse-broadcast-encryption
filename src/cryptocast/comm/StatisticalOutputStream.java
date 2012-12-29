package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wrapper around an instance of {@link OutChannel} that counts outgoing bytes.
 */
public class StatisticalOutputStream extends OutputStream {
    private OutputStream inner;
    private int sentBytes = 0;

    /**
     * Initializes the proxy
     * @param inner The wrapped channel
     */
    public StatisticalOutputStream(OutputStream inner) {
        this.inner = inner; 
    }

    /**
     * @return The number of sent bytes
     */
    public int getSentBytes() {
        return sentBytes;
    }
    
    @Override 
    public void write(byte[] buffer, int offset, int len) throws IOException {
        sentBytes += len;
        inner.write(buffer, offset, len);
    }
    
    @Override 
    public void write(int b) throws IOException {
        sentBytes++;
        inner.write(b);
    }
}
