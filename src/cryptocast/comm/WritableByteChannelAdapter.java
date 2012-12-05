package cryptocast.comm;

import java.nio.WritableByteChannel;

/**
 * Adapter to use a WritableByteChannel (for example, a socket instance) as an OutChannel.
 */
public class WritableByteChannelAdapter implements OutChannel {
    /**
     * Initializes the adapter
     * @param inner The wrapped instance
     */
    public WritableByteChannelAdapter(WritableByteChannel inner) { }

    /** {@inheritDoc} */
    public void send(ByteBuffer data) { }
}
