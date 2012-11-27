package broadcastenc.transport;

/** An abstract communication channel where you can put binary data in.
 */
public interface OutChannel {
    /** Puts a chunk of binary data into the channel.
     * @param data The data to send
     */
    public void send(byte[] data);
}

