package cryptocast.comm;

import java.nio.channels.ReadableByteChannel;

/**
 * Adapter to use an instance of {@link ReadableByteChannel} (for example, a file or socket instance) 
 * as an {@link InChannel}.
 */
public class ReadableByteChannelAdapter implements InChannel {
    /**
     * Initializes the adapter
     * @param inner The wrapped instance
     */
    public ReadableByteChannelAdapter(ReadableByteChannel inner) { }

    /**
     * Receives data.
     * @param size Maximum amount of bytes to read
     * @param buffer The target buffer
     */
    public void recv(int size, byte[] buffer) { }
}
