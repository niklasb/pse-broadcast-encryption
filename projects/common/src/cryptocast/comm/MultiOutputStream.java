package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * Multiplexes several instances of {@link OutputStream}s so that they can be used as a single
 * destination.
 */
public class MultiOutputStream extends OutputStream {
    public enum ErrorHandling {
        REMOVE,
        IGNORE,
        THROW,
    };
    List<OutputStream> channels = new ArrayList<OutputStream>();
    ErrorHandling errHandling;
    
    public MultiOutputStream(ErrorHandling errHandling) {
        this.errHandling = errHandling;
    }

    /**
     * Adds the given channel to the list of receivers.
     * @param channel The channel to add
     */
    public void addChannel(OutputStream channel) {
        channels.add(channel);
    }
    /**
     * Removes the given channel from the list of receivers.
     * @param channel The channel to remove
     */
    public void removeChannel(OutputStream channel) {
        channels.remove(channel);
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        for (OutputStream chan : channels) {
            try {
                chan.write(data, offset, len);
            } catch (IOException e) {
                if (errHandling == ErrorHandling.THROW) {
                    throw e;
                } else if (errHandling == ErrorHandling.REMOVE) {
                    removeChannel(chan);
                }
            }
        }
    }
    
    @Override
    public void write(int b) throws IOException {
        for (OutputStream chan : channels) {
            chan.write(b);
        }
    }
}
