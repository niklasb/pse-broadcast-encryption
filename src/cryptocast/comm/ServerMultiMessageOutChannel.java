package cryptocast.comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * This class implements channel-based communication via TCP.
 */
public class ServerMultiMessageOutChannel extends MessageOutChannel implements Runnable {
    private static final Logger log = LoggerFactory
            .getLogger(ServerMultiMessageOutChannel.class);
    
    private MultiOutputStream multi;
    private Function<Throwable, Boolean> excHandler;
    private ServerSocket server;
    private MessageOutChannel out;
    
    /**
     * Creates an instance of a multicast server which uses the given socket.
     * @param server Server socket
     */
    public ServerMultiMessageOutChannel(ServerSocket server,
                                        Function<Throwable, Boolean> excHandler) {
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
                log.trace("Caught exception in accept loop. Calling handler", e);
                if (!excHandler.apply(e)) {
                    return;
                }
                log.debug("Handler told us to ignore the exception.");
            }
        }
    }

    @Override
    public void sendMessage(byte[] data, int offset, int len) throws IOException {
        out.sendMessage(data, offset, len);
    }
}
