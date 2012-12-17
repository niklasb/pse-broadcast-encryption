package cryptocast.comm;

import java.nio.channels.WritableByteChannel;

/**
 * Adapter to use an instance of {@link WritableByteChannel} (for example, a file or socket instance) as
 * an {@link OutChannel}.
 */
public class WritableByteChannelAdapter implements OutChannel {
    /**
     * Initializes the adapter.
     * @param inner The wrapped instance
     */
    public WritableByteChannelAdapter(WritableByteChannel inner) { }

    /**
     * Sends the given data.
     * @param data The data to send
     */
    public void send(byte[] data) { }
}
