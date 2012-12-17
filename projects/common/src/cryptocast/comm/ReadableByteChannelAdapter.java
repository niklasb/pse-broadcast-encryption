package cryptocast.comm;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Adapter to use an instance of {@link ReadableByteChannel} (for example, a file or socket instance) 
 * as an {@link InChannel}.
 */
public class ReadableByteChannelAdapter extends InChannel {
    private ReadableByteChannel inner;

    /**
     * Initializes the adapter.
     * @param inner The wrapped instance
     */
    public ReadableByteChannelAdapter(ReadableByteChannel inner) {
        this.inner = inner; 
    }

    /**
     * Receives data.
     * @param size Maximum amount of bytes to read
     * @param buffer The target buffer
     */
    @Override
    public int recv(ByteBuffer buffer) throws IOException {
        return inner.read(buffer);
    }
}
