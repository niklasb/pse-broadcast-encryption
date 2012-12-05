package cryptocast.comm;

import java.nio.ReadableByteChannel;

/**
 * Adapter to use a ReadableByteChannel (for example, a socket instance) as an InChannel.
 */
public class ReadableByteChannelAdapter implements InChannel {
    /**
     * Initializes the adapter
     * @param inner The wrapped instance
     */
    public ReadableByteChannelAdapter(ReadableByteChannel inner) { }

    /** {@inheritDoc} */
    public ByteBuffer recv(int size) { return null; }
}
