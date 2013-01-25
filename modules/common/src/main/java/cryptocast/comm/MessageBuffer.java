package cryptocast.comm;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A message channgel implementation that just buffers all incoming messages
 * in memory and returns them when a message is received. For testing purposes.
 */
public class MessageBuffer extends MessageOutChannel implements MessageInChannel {
    private Queue<byte[]> messages = new LinkedBlockingQueue<byte[]>();
    private boolean closed = false;
    private boolean blocking = true;

    /**
     * Sets the blocking mode of this message buffer
     *
     * @param block The block to set.
     */
    public void setBlocking(boolean block) {
        this.blocking = block;
    }

    @Override
    public byte[] recvMessage() {
        if (messages.isEmpty()) {
            if (closed) {
                return null;
            } else if (blocking) {
                while (messages.isEmpty()) {}
            }
        }
        return messages.isEmpty() ? null : messages.remove();
    }

    @Override
    public void sendMessage(byte[] data, int offset, int len) {
        messages.add(Arrays.copyOfRange(data, offset, len));
    }

    public void close() {
        closed = true;
    }
}
