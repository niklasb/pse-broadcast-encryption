package broadcastenc.transport;

import java.nio.ByteBuffer;

/** Wraps a byte-based OutChannel and allows to use it as a message-based
 * channel.
 */
class MessageOutChannel {
    /**
     * Creates a new MessageOutChannel with the given OutChannel as inner channel.
     * @param inner the OutChannel which will be wrapped
     */
    public MessageOutChannel(OutChannel inner) { }
    
    /**
     * Sends the given message via the channel.
     * @param data the data to send
     */
    public void send(ByteBuffer bytes) { }
}
