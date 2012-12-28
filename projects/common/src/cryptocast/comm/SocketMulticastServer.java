package cryptocast.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.util.Callback;

/**
 * This class implements channel-based communication via TCP.
 */
public class SocketMulticastServer extends OutputStream implements Runnable {
    private MultiOutputStream multi;
    private Callback<Throwable> excHandler;
    private ServerSocket server;
    private static final Logger log = LoggerFactory
            .getLogger(SocketMulticastServer.class);
    
    /**
     * Creates an instance of a multicast server which uses the given socket.
     * @param socket Server socket
     */
    public SocketMulticastServer(ServerSocket server,
                                 Callback<Throwable> excHandler) {
        this.multi = new MultiOutputStream(MultiOutputStream.removeOnError);
        this.server = server;
        this.excHandler = excHandler;
    };
    
    @Override
    public void run() {
        log.debug("Starting accept loop");
        for(;;) {
            Socket socket;
            try {
                socket = server.accept();
                multi.addChannel(socket.getOutputStream());
                log.debug("New client connected!");
            } catch (Throwable e) {
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
    public void write(int b) throws IOException {
        multi.write(b);
    }
    
    @Override
    public void close() throws IOException {
        multi.close();
    }
    
    @Override
    public void flush() throws IOException {
        multi.flush();
    }
}
