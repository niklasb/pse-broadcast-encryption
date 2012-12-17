package cryptocast.comm;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Multiplexes several instances of {@link OutChannel}s so that they can be used as a single
 * destination.
 */
public class MultiOutChannel implements OutChannel {
    List<OutChannel> channels = new ArrayList<OutChannel>();
    
    /**
     * Adds the given channel to the list of receivers.
     * @param channel The channel to add
     */
    public void addChannel(OutChannel channel) {
        channels.add(channel);
    }
    /**
     * Removes the given channel from the list of receivers.
     * @param channel The channel to remove
     */
    public void removeChannel(OutChannel channel) {
        channels.remove(channel);
    }

    /**
     * Sends the given data.
     * @param data The data to send
     */
    @Override
    public void send(byte[] data) throws IOException {
        for (OutChannel chan : channels) {
            chan.send(data);
        }
    }
}
