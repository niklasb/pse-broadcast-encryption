package cryptocast.comm;

import java.nio.ByteBuffer;

/**
 * A byte-based communication channel from which data can be received.
 */
public interface InChannel {
    /**
     * Receives data.
     * @param size maximum amount of bytes to read
     * @return the received data
     */
    public ByteBuffer recv(int size);
}
