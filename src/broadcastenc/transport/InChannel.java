package broadcastenc.transport;

/** An abstract communication channel where you can pull binary data out.
 */
public interface InChannel {
    /** Gets data from the channel.
     * @param size The desired amount of data to receive
     * @return a chunk of binary data with at most `size` bytes
     */
    public byte[] recv();
}

