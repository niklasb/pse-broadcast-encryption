package cryptocast.comm;

import java.io.OutputStream;

public class MemoryOutputStream extends OutputStream {
    private byte[] outbuf;
    private int sent = 0;

    public MemoryOutputStream(byte[] outbuf) {
        this.outbuf = outbuf;
    }
    public MemoryOutputStream(int size) {
        this.outbuf = new byte[size];
    }
    
    public byte[] getSentBytes() {
        byte[] res = new byte[sent];
        System.arraycopy(outbuf, 0, res, 0, sent);
        return res;
    }

    @Override
    public void write(int b) {
        outbuf[sent++] = (byte)b;
    }
}
