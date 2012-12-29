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
        int b = inner.read();
        if (b < 0) { return b; }
        receivedBytes++;
        return b;
    }
    
    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException {
        int received = inner.read(buffer, offset, len);
        if (received < 0) { return received; }
        receivedBytes += received;
        return received;
    }
}
