package cryptocast.comm;

import java.io.InputStream;

/**
 * Adapter to use an InputStream as an InChannel, for example to stream from
 * files.
 */
public class InputStreamChannel implements InChannel {
    /**
     * Initializes the adapter
     * @param input The wrapped stream
     */
    public InputStreamChannel(InputStream input) {}

    /**
     * Receives data.
     * @param size maximum amount of bytes to read
     * @param buffer the target buffer
     */
    public void recv(int size, byte[] buffer) {}
}
