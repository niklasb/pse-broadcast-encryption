package broadcastenc.transport;

/** Multiplexes several OutChannels so that they can be used as a single
 * destination.
 */
public class MultiOutChannel implements OutChannel {
    /**
     * Sends the given data to all stored channels.
     * @param data the data to send
     */
    public void send(byte[] data) { }
    /**
     * Adds the given channel to the list of receivers.
     * @param channel the channel to add
     */
    public void addChannel(OutChannel channel) { }
    /**
     * Removes the given channel from the list of receivers.
     * @param channel the channel to remove
     */
    public void removeChannel(OutChannel channel) { }
}
