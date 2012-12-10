package cryptocast.comm;

import java.nio.channels.ReadableByteChannel;

/**
 * Adapter to use a {@link ReadableByteChannel} (for example, a file or socket instance) as an
 * {@link InChannel}.
 */
public class ReadableByteChannelAdapter implements InChannel {
    /**
     * Initializes the adapter
     * @param inner The wrapped instance
     */
    public ReadableByteChannelAdapter(ReadableByteChannel inner) { }

    /**
     * Receives data.
     * @param size maximum amount of bytes to read
     * @param buffer the target buffer
     */
    public void recv(int size, byte[] buffer) {}
}
