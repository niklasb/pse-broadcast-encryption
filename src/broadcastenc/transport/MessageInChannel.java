package broadcastenc.transport;

/** Wraps a byte-based InChannel and allows to use it as a message-based
 * channel.
 */
public class MessageInChannel {
    public MessageInChannel(InChannel inner) { }
    public byte[] recv() { }
}
