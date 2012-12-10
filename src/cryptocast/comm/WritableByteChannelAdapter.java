package cryptocast.comm;

import java.nio.channels.WritableByteChannel;

/**
 * Adapter to use a {@link WritableByteChannel} (for example, a file or socket instance) as
 * an {@link OutChannel}.
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
