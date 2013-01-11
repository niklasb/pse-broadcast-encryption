package cryptocast.comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.util.Callback;

/**
 * This class implements channel-based communication via TCP.
 */
public class ServerMultiMessageOutChannel extends MessageOutChannel implements Runnable {
    private MultiOutputStream multi;
    private Callback<Throwable> excHandler;
    private ServerSocket server;
    private MessageOutChannel out;
    private static final Logger log = LoggerFactory
            .getLogger(ServerMultiMessageOutChannel.class);
    
    /**
     * Creates an instance of a multicast server which uses the given socket.
     * @param server Server socket
     */
    public ServerMultiMessageOutChannel(ServerSocket server,
                                        Callback<Throwable> excHandler) {
        this.multi = new MultiOutputStream(MultiOutputStream.removeOnError);
        this.out = new StreamMessageOutChannel(multi);
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
    public void sendMessage(byte[] data, int offset, int len) throws IOException {
        out.sendMessage(data, offset, len);
    }
}
