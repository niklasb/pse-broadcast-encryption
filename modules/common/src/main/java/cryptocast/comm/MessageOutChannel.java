package cryptocast.comm;

import java.io.IOException;

/**
 * A byte-based communication channel where data can be sent to.
 */
public abstract class MessageOutChannel {
	
	/**
	 * Sends the data.
	 * 
	 * @param data The data to send.
	 * @param offset The start offset in array data at which the data is written.
     * @param len The maximum number of bytes to read.
	 * @throws IOException
	 */
    public abstract void sendMessage(byte[] data, int offset, int len) throws IOException;
    
    /**
     * Sends the data.
     * 
     * @param data The data to send.
     * @throws IOException
     */
    public void sendMessage(byte[] data) throws IOException {
        sendMessage(data, 0, data.length);
    }
}
