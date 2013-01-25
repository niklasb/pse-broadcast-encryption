package cryptocast.comm;

import java.io.IOException;

/**
 * A byte-based communication channel where data can be sent to.
 */
public abstract class MessageOutChannel {
    /**
     * Sends a message to the channel.
     *
     * @param data The data to send.
     * @param offset The offset of the payload in the array.
     * @param len The length of the message.
     * @throws IOException
     */
    public abstract void sendMessage(byte[] data, int offset, int len) throws IOException;

    /**
     * Sends a message to the channel.
     *
     * @param data The data to send.
     * @throws IOException
     */
    public void sendMessage(byte[] data) throws IOException {
        sendMessage(data, 0, data.length);
    }
}
