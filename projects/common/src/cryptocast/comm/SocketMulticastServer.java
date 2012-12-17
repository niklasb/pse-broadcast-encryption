package cryptocast.comm;

import java.net.ServerSocket;

/**
 * This class implements channel-based communication via TCP.
 */
class SocketMulticastServer implements OutChannel  {
    /**
     * Creates an instance of a multicast server which uses the given socket.
     * @param socket Server socket
     */
    public SocketMulticastServer(ServerSocket socket) { };

    /**
     * Sends bytes via the channel.
     * @param data The data to send
     */
    public void send(byte[] data) { }
}
