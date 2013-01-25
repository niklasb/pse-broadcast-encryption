package cryptocast.comm;

import java.io.IOException;

/**
 * A byte-based communication channel from which data can be received.
 */
public interface MessageInChannel {
    /**
     * @return The data or <code>null</code>, if the end-of-file is reached.
     * @throws IOException
     */
    public byte[] recvMessage() throws IOException;
}
