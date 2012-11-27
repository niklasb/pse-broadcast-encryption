package broadcastenc.transport;

/** Wraps a byte-based OutChannel and allows to use it as a message-based
 * channel.
 */
class MessageOutChannel {
    public MessageOutChannel(OutChannel inner) { }
    public void send(byte[] message) { }
}
