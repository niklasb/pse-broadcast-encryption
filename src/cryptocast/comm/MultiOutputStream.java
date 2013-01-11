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
	 * 
	 *	Define an interface of an error handler
	 */
	public static interface ErrorHandler {
       
		/**
         * Handle errors.
         * 
         * @param multi The multi output stream.
         * @param channel The channel.
         * @param exc The exception to throw. 
         * @throws IOException
         */
		public void handle(MultiOutputStream multi, OutputStream channel, 
                IOException exc) throws IOException;
    }
    
	/**
	 * Creates an instance of ErrorHandler.
	 */
    public static ErrorHandler propagateError = new ErrorHandler() {
        @Override
        public void handle(MultiOutputStream multi, OutputStream channel, 
                IOException exc) throws IOException {
            throw exc;
        }
    };
    
    /**
     * Creates an instance of ErrorHandler.
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
     * Creates an instance of MultiOutputStream with the given parameter.
     * 
     * @param errHandler The error
     */
    public MultiOutputStream(ErrorHandler errHandler) {
        this.errHandler = errHandler;
    }
    
    /**
     * Creates an instance of MultiOutputStream.
     */
    public MultiOutputStream() {
        this.errHandler = propagateError;
    }

    /**
     * Returns an immutable list of all output channels
     * @return an immutable list of all output channels
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
