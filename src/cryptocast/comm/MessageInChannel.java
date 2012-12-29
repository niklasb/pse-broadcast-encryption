package cryptocast.comm;

import java.io.IOException;

public interface MessageInChannel {
    public byte[] recvMessage() throws IOException;
}
