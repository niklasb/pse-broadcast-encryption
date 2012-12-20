package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;

public class PacketizingOutputStream extends OutputStream {
    private MessageOutChannel inner;
    byte[] messageBuffer;
    int bufferOffset = 0;

    public PacketizingOutputStream(MessageOutChannel inner, 
                                   int bufsize) {
        this.inner = inner;
        this.messageBuffer = new byte[bufsize];
    }

    @Override
    public void flush() throws IOException {
        if (bufferOffset == 0) { return; }
        inner.sendMessage(messageBuffer, 0, bufferOffset);
        bufferOffset = 0;
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        while (len > 0) {
            int copy = Math.min(len, messageBuffer.length - bufferOffset);
            System.arraycopy(data, offset, messageBuffer, bufferOffset, copy);
            bufferOffset += copy;
            offset += copy;
            len -= copy;
            if (bufferOffset == messageBuffer.length) {
                flush();
            }
        }
    }
    
    @Override
    public void write(int b) throws IOException {
        messageBuffer[bufferOffset++] = (byte)b;
        if (bufferOffset == messageBuffer.length) {
            flush();
        }
    }
}
