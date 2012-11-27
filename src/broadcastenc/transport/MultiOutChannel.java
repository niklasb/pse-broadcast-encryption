package broadcastenc.transport;

/** Multiplexes several OutChannels so that they can be used as a single
 * destination.
 */
public class MultiOutChannel implements OutChannel {
    public void send(byte[] data) { }
    public void addChannel(OutChannel channel) { }
    public void removeChannel(OutChannel channel) { }
}
