package cryptocast.comm;

/**
 * Wraps a byte-based instance of {@link OutChannel} and allows to use it as a message-based
 * channel.
 */
public class MessageOutChannel {
    /**
     * Creates a new instance of MessageOutChannel with the given OutChannel as inner channel.
     * @param inner The OutChannel which will be wrapped.
     */
    public MessageOutChannel(OutChannel inner) { }

    /**
     * Sends the given message via the channel.
     * @param data The data to send.
     */
    public void sendMessage(byte[] data) { }
}
