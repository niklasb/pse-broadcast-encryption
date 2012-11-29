package broadcastenc.transport;

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
    public void send(Message message) { }
    
    /**
     * Packetizes the given Message as byte array to send it via a channel.
     * @param message the message to packetize
     * @return the packetized message
     */
    private byte[] packetize(Message message) { }
}
