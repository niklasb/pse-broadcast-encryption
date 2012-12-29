package cryptocast.comm;

import java.io.IOException;

public abstract class MessageOutChannel {
    public abstract void sendMessage(byte[] data, int offset, int len) throws IOException;
    
    public void sendMessage(byte[] data) throws IOException {
        sendMessage(data, 0, data.length);
    }
}
