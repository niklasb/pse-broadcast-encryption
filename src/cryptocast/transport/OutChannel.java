package cryptocast.transport;

import java.nio.ByteBuffer;

public interface OutChannel {
    /**
     * Sends the given data.
     * @param data the data to send
     */
    public void send(ByteBuffer data);
}
