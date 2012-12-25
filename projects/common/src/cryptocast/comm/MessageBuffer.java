package cryptocast.comm;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageBuffer extends MessageOutChannel implements MessageInChannel {
    private Queue<byte[]> messages = new LinkedBlockingQueue<byte[]>();
    private boolean closed = false;
    private boolean blocking = true;
    
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
