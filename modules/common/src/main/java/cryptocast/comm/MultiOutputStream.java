package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

/**
 * Multiplexes several instances of {@link OutputStream}s so that they can be used as a single
 * destination.
 */
public class MultiOutputStream extends OutputStream {
    /**
     * An error handler.
     */
    public static interface ErrorHandler {
        /**
         * Handle an error.
         *
         * @param multi The multi output stream.
         * @param channel The channel.
         * @param exc The exception that occured.
         * @throws IOException
         */
        public void handle(MultiOutputStream multi, OutputStream channel, 
                IOException exc) throws IOException;
    }

    /**
     * An error handler that just rethrows the exception, effectively
     * passing the error to the caller of {@link send}.
     */
    public static ErrorHandler propagateError = new ErrorHandler() {
        @Override
        public void handle(MultiOutputStream multi, OutputStream channel,
                IOException exc) throws IOException {
            throw exc;
        }
    };

    /**
     * An error handler that surpresses the exception and removes the faulty
     * channel.
     */
    public static ErrorHandler removeOnError = new ErrorHandler() {
        @Override
        public void handle(MultiOutputStream multi, OutputStream channel, 
                IOException exc) {
            multi.removeChannel(channel);
        }
    };

    List<OutputStream> channels = new ArrayList<OutputStream>();
    ErrorHandler errHandler;

    /**
     * Creates an instance of MultiOutputStream.
     *
     * @param errHandler An error handler instance.
     */
    public MultiOutputStream(ErrorHandler errHandler) {
        this.errHandler = errHandler;
    }

    /**
     * Creates an instance of MultiOutputStream that passes on all errors to
     * the caller.
     */
    public MultiOutputStream() {
        this.errHandler = propagateError;
    }

    /**
     * @return the list of output channels
     */
    public ImmutableList<OutputStream> getChannels() {
        return ImmutableList.copyOf(channels);
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
