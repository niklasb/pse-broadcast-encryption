package cryptocast.comm;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class BufferBackedChannel extends InChannel implements OutChannel {
    private ByteBuffer inbuf;
    private ByteBuffer outbuf;
    private int maxRecvSize;
    private int sent = 0;

    private BufferBackedChannel(ByteBuffer inbuf, ByteBuffer outbuf,
                                int maxRecvSize) {
        this.inbuf = inbuf;
        this.outbuf = outbuf;
        this.maxRecvSize = maxRecvSize;
    }

    public static BufferBackedChannel createInChannel(byte[] inbuf,
                                                      int maxRecvSize) {
        return new BufferBackedChannel(ByteBuffer.wrap(inbuf), null,
                                       maxRecvSize);
    }
    
    public static BufferBackedChannel createOutChannel(int size) {
        return new BufferBackedChannel(null, ByteBuffer.allocate(size), 0);
    }

    public static BufferBackedChannel createPipe(int size) {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        return new BufferBackedChannel(buffer, buffer.duplicate(), 4096);
    }

    public byte[] getSentBytes() {
        byte[] all = outbuf.array();
        byte[] res = new byte[sent];
        System.arraycopy(all, 0, res, 0, sent);
        return res;
    }

    @Override
    public void send(byte[] data) {
        outbuf.put(data);
        sent += data.length;
    }

    @Override
    public int recv(ByteBuffer buffer) {
        int received = 0;
        while (inbuf.position() < inbuf.limit() && buffer.remaining() > 0
                                                && received < maxRecvSize) {
            byte x = inbuf.get();
            buffer.put(x);
            received++;
        }
        return received;
    }

    public static byte[] str2bytes(String str) {
        try {
            return str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // cannot happen, as ISO-8859-1 must be supported
            // according to specs
            return null;
        }
    }

    public static String bytes2str(byte[] bytes) {
        try {
            return new String(bytes, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // see above
            return null;
        }
    }
}
