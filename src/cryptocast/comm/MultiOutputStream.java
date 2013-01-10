package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multiplexes several instances of {@link OutputStream}s so that they can be used as a single
 * destination.
 */
public class MultiOutputStream extends OutputStream {
    static Logger log = LoggerFactory.getLogger(MultiOutputStream.class);

    public static interface ErrorHandler {
        public void handle(MultiOutputStream multi, OutputStream channel, 
                IOException exc) throws IOException;
    }
    
    public static ErrorHandler propagateError = new ErrorHandler() {
        @Override
        public void handle(MultiOutputStream multi, OutputStream channel, 
                IOException exc) throws IOException {
            log.debug("Channel removed because of error", exc);
            throw exc;
        }
    };
    
    public static ErrorHandler removeOnError = new ErrorHandler() {
        @Override
        public void handle(MultiOutputStream multi, OutputStream channel, 
                IOException exc) {
            multi.removeChannel(channel);
        }
    };
    
    List<OutputStream> channels = new ArrayList<OutputStream>();
    ErrorHandler errHandler;

    public MultiOutputStream(ErrorHandler errHandler) {
        this.errHandler = errHandler;
    }
    
    public MultiOutputStream() {
        this.errHandler = propagateError;
    }

    public synchronized ImmutableList<OutputStream> getChannels() {
        return ImmutableList.copyOf(channels);
    }

    /**
     * Adds the given channel to the list of receivers.
     * @param channel The channel to add
     */
    public synchronized void addChannel(OutputStream channel) {
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
    public synchronized void write(byte[] data, int offset, int len) throws IOException {
        for (OutputStream chan : getChannels()) {
            try {
                chan.write(data, offset, len);
            } catch (IOException e) {
                errHandler.handle(this, chan, e);
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
