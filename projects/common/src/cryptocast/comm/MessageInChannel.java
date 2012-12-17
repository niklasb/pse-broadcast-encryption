package cryptocast.comm;

/**
 * Wraps a byte-based instance of {@link InChannel} and allows to use it as a message-based
 * channel.
 */
public class MessageInChannel {
    /**
     * Creates an instance of MessageInChannel which wraps the given inner channel.
     * @param inner The wrapped channel
     */
    public MessageInChannel(InChannel inner) { }
   /**
    * Receives a message via the channel.
    * @return The received data
    */
    public byte[] recvMessage() {
        return null;
    }
}
