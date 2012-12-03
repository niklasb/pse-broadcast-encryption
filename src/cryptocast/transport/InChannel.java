package cryptocast.transport;

import java.nio.ByteBuffer;

public interface InChannel {
    /**
     * Receives data.
     * @param size maximum amount of bytes to read
     * @return the received data
     */
    public ByteBuffer recv(int size);
}
