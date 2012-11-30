package broadcastenc.transport;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/** Wraps a byte-based InChannel and allows to use it as a message-based
 * channel.
 */
public class MessageInChannel {
    /**
     * Creates a MessageInChannel which wraps the given inner channel.
     * @param inner the wrapped channel
     */
    public MessageInChannel(ReadableByteChannel inner) { }
   /**
    * Receives bytes via the channel.
    * @return the received bytes
    */
    public ByteBuffer recv() {return null;}
}
