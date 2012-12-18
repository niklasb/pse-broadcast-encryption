package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wraps a byte-based instance of {@link OutputStream} and allows to use it as a message-based
 * channel.
 */
public class MessageOutChannel {
    private OutputStream inner;

    /**
     * Creates a new instance of MessageOutChannel with the given OutChannel as inner channel.
     * @param inner The OutChannel which will be wrapped.
     */
    public MessageOutChannel(OutputStream inner) {
        this.inner = inner; 
    }

    /**
     * Sends the given message via the channel.
     * @param data The data to send.
     */
    public void sendMessage(byte[] data) throws IOException {
        ByteBuffer size = ByteBuffer.allocate(4);
        size.order(ByteOrder.BIG_ENDIAN);
        size.putInt(data.length);
        inner.write(size.array());
        inner.write(data);
    }
}
