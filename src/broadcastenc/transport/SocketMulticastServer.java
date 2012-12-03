package broadcastenc.transport;

import java.net.ServerSocket;
import java.nio.ByteBuffer;

/**
 * This class implements channel-based communication via TCP.
 */
class SocketMulticastServer implements OutChannel  {

    /**
     * Creates a multicast server which uses the given socket.
     * @param socket server socket
     */
    public SocketMulticastServer(ServerSocket socket) { };

    /**
     * Sends bytes via the channel.
     * @param data the data to send
     */
    public void send(ByteBuffer data) { }
}
