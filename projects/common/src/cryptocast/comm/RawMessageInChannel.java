package cryptocast.comm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wraps a byte-based instance of {@link InputStream} and allows to use it as a
 * message-based channel.
 */
public class RawMessageInChannel {
    private InputStream inner;

    /**
     * Creates an instance of MessageInChannel which wraps the given inner
     * channel.
     * @param inner The wrapped channel
     */
    public RawMessageInChannel(InputStream inner) {
        this.inner = inner;
    }

    /**
     * Receives a message via the channel or null on EOF.
     * @return The received data
     * @throws IOException if the message header or the message is malformed
     */
    public byte[] recvMessage() throws InterruptedException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        int recv = StreamUtils.readall(inner, buffer.array(), 0, 4);
        if (recv == 0) {
            return null;
        } else if (recv < 4) {
            throw new IOException("Malformed message size (unexpected EOF)");
        }
        buffer.rewind();
        int size = buffer.getInt();
        byte[] result = new byte[size];
        if (size != StreamUtils.readall(inner, result, 0, size)) {
            throw new IOException("Malformed message (unexpected EOF)");
        }
        return result;
    }
}
