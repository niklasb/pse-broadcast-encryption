package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import cryptocast.util.Callback;

/**
 * This class implements channel-based communication via TCP.
 */
class SocketMulticastServer extends OutputStream implements Runnable {
    MultiOutputStream multi = new MultiOutputStream();
    Callback<IOException> excHandler;
    ServerSocket server;
    
    /**
     * Creates an instance of a multicast server which uses the given socket.
     * @param socket Server socket
     */
    public SocketMulticastServer(ServerSocket server,
                                 Callback<IOException> excHandler) { 
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

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        multi.write(data, offset, len);
    }
    
    @Override
    public void write(byte[] data) throws IOException {
        multi.write(data);
    }
    
    @Override
    public void write(int b) throws IOException {
        multi.write(b);
    }
}
