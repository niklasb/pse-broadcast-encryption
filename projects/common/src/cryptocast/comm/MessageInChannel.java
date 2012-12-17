package cryptocast.comm;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wraps a byte-based instance of {@link InChannel} and allows to use it as a
 * message-based channel.
 */
public class MessageInChannel {
    private InChannel inner;

    /**
     * Creates an instance of MessageInChannel which wraps the given inner
     * channel.
     * @param inner The wrapped channel
     */
    public MessageInChannel(InChannel inner) {
        this.inner = inner;
    }

    /**
     * Receives a message via the channel.
     * @return The received data
     */
    public byte[] recvMessage() throws InterruptedException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        inner.recvall(buffer);
        buffer.rewind();
        int size = buffer.getInt();
        buffer = ByteBuffer.allocate(size);
        inner.recvall(buffer);
        return buffer.array();
    }
}
