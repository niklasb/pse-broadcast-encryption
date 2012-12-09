package cryptocast.comm;

import java.nio.channels.WritableByteChannel;

/**
 * Adapter to use a WritableByteChannel (for example, a socket instance) as an OutChannel.
 */
public class WritableByteChannelAdapter implements OutChannel {
    /**
     * Initializes the adapter
     * @param inner The wrapped instance
     */
    public WritableByteChannelAdapter(WritableByteChannel inner) { }

    /**
     * Sends the given data.
     * @param data the data to send
     */
    public void send(byte[] data) { }
}
