package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

/**
 * This class implements channel-based communication via TCP.
 */
class SocketMulticastServer extends OutputStream  {
    /**
     * Creates an instance of a multicast server which uses the given socket.
     * @param socket Server socket
     */
    public SocketMulticastServer(ServerSocket socket) { };

    @Override
    public void write(int b) throws IOException {
        // TODO Auto-generated method stub
    }
}
