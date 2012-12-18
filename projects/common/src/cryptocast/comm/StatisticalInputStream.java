package cryptocast.comm;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapper around an instance of {@link InputStream} that counts incoming bytes.
 */
public class StatisticalInputStream extends InputStream {
    private int receivedBytes = 0;
    private InputStream inner;
    
    /**
     * Initializes the proxy.
     * @param inner The wrapped channel
     */
    public StatisticalInputStream(InputStream inner) {
        this.inner = inner;
    }

    /**
     * @return The number of received bytes
     */
    public int getReceivedBytes() {
        return receivedBytes;
    }

    @Override
    public int read() throws IOException {
        receivedBytes++;
        return inner.read();
    }
    
    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException {
        int received = inner.read(buffer, offset, len);
        receivedBytes += received;
        return received;
    }
    
    @Override
    public int read(byte[] buffer) throws IOException {
        return inner.read(buffer, 0, buffer.length);
    }
}
