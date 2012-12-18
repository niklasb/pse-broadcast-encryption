package cryptocast.comm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wraps a byte-based instance of {@link InputStream} and allows to use it as a
 * message-based channel.
 */
public class MessageInChannel {
    private InputStream inner;

    /**
     * Creates an instance of MessageInChannel which wraps the given inner
     * channel.
     * @param inner The wrapped channel
     */
    public MessageInChannel(InputStream inner) {
        this.inner = inner;
    }

    /**
     * Receives a message via the channel.
     * @return The received data
     */
    public byte[] recvMessage() throws InterruptedException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        StreamUtils.readall(inner, buffer.array(), 0, 4);
        buffer.rewind();
        int size = buffer.getInt();
        byte[] result = new byte[size];
        StreamUtils.readall(inner, result, 0, size);
        return result;
    }
}
