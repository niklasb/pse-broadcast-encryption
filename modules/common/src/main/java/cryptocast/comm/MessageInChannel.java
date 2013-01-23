package cryptocast.comm;

import java.io.IOException;

/**
 * A byte-based communication channel from which data can be received.
 */
public interface MessageInChannel {
	/**
	 * Receives data.
	 * @return The data 
	 * @throws IOException
	 */
    public byte[] recvMessage() throws IOException;
}
