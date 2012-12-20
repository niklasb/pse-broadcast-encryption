package cryptocast.comm;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cryptocast.util.Callback;

/**
 * This class implements channel-based communication via TCP.
 */
class SocketMulticastServer extends FilterOutputStream implements Runnable {
    MultiOutputStream multi = new MultiOutputStream(
            MultiOutputStream.ErrorHandling.REMOVE);
    Callback<IOException> excHandler;
    ServerSocket server;
    
    /**
     * Creates an instance of a multicast server which uses the given socket.
     * @param socket Server socket
     */
    public SocketMulticastServer(ServerSocket server,
                                 Callback<IOException> excHandler) { 
        super(null);
        this.out = multi;
        this.server = server;
        this.excHandler = excHandler;
    };

    @Override
    public void run() {
        for(;;) {
            Socket socket;
            try {
                socket = server.accept();
                multi.addChannel(socket.getOutputStream());
            } catch (IOException e) {
                excHandler.handle(e);
                return;
            }
        }
    }
}
