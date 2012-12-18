package cryptocast.comm;

import java.io.InputStream;

public class MemoryInputStream extends InputStream {
    private byte[] inbuf;
    private int maxReadSize;
    int inbufOffset = 0;

    public MemoryInputStream(byte[] inbuf, int maxReadSize) {
        this.inbuf = inbuf;
        this.maxReadSize = maxReadSize;
    }

    @Override
    public int read(byte[] buffer, int offset, int len) {
        int received = 0;
        while (inbufOffset < inbuf.length && len > 0
                                          && received < maxReadSize) {
            buffer[offset++] = inbuf[inbufOffset++];
            len--;
            received++;
        }
        if (received == 0 && len > 0) {
            return -1;
        }
        return received;
    }

    @Override
    public int read(byte[] buffer) {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read() {
        byte[] res = new byte[1];
        if (read(res) < 0) { return -1; }
        return res[0];
    }
}
