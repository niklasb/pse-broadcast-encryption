package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wraps a byte-based instance of {@link OutputStream} and allows to use it as a message-based
 * channel.
 */
public class StreamMessageOutChannel extends MessageOutChannel {
    private OutputStream inner;

    /**
     * Creates a new instance of MessageOutChannel with the given OutChannel as inner channel.
     * 
     * @param inner The OutChannel which will be wrapped.
     */
    public StreamMessageOutChannel(OutputStream inner) {
        this.inner = inner; 
    }

    /**
     * Sends the given message via the channel using a single write.
     * 
     * @param data The data to send.
     * @param offset The start offset in array data at which the data is written.
     * @param len The maximum number of bytes to read.
     */
    public void sendMessage(byte[] data, int offset, int len) throws IOException {
        ByteBuffer msg = ByteBuffer.allocate(4 + len);
        msg.order(ByteOrder.BIG_ENDIAN);
        msg.putInt(len);
        msg.put(data, offset, len);
        inner.write(msg.array());
    }
}
