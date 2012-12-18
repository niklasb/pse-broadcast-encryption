package cryptocast.comm;

import java.io.IOException;

public interface MessageInChannel {
    public void sendMessage(byte[] data) throws IOException;
}
