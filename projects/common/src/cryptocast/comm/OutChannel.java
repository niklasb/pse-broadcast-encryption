package cryptocast.comm;

import java.io.IOException;

/**
 * A byte-based communication channel where data can be sent to.
 */
public interface OutChannel {
    /**
     * Sends the given data.
     * @param data The data to send
     */
    public void send(byte[] data) throws IOException;
}
