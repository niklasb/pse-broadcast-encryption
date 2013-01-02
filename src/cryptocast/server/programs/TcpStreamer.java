package cryptocast.server.programs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import cryptocast.comm.ThrottledOutputStream;
import cryptocast.server.OptParse;

/**
 * The main method to start the server.
 */
public final class TcpStreamer {
    private static final Logger log = LoggerFactory
            .getLogger(TcpStreamer.class);
    
    static class Options extends OptParse.WithHelp {
        @Parameter(names = { "-p", "--port" }, description = "Bind port")
        private int listenPort = 21337;

        @Parameter(names = { "-l", "--listen-address"}, description = "Bind address")
        private String listenAddr = "127.0.0.1";
        
        @Parameter(required = true, names = { "-f", "--file" }, description = "The file to stream")
        private File file;
        
        @Parameter(names = { "-t", "--throttle" }, description = "Throttle to the given number of bytes per second")
        private long maxBps = 0;
        
        @Parameter(names = { "-s", "--skip" }, description = "Number of bytes to skip")
        private int skip = 0;
    }

    private static class ConnectionHandler implements Runnable {
        private Socket sock;
        private File file;
        private int skip;
        private long maxBps;
        
        public ConnectionHandler(Socket sock, File file, int skip, long maxBps) {
            this.sock = sock;
            this.file = file;
            this.skip = skip;
            this.maxBps = maxBps;
        }
        
        public void run() {
            try {
                OutputStream out = sock.getOutputStream();
                if (maxBps > 0) {
                    out = new ThrottledOutputStream(out, maxBps);
                }
                InputStream in = new FileInputStream(file);
                log.debug("Skipping {} bytes", skip);
                for (int i = 0; i < skip; ++i) {
                    in.read();
                }
                try {
                    byte[] buffer = new byte[0x1000];
                    int received;
                    while (0 <= (received = in.read(buffer))) {
                        out.write(buffer, 0, received);
                    }
                } finally {
                    in.close();
                }
            } catch (Exception e) {
                log.error("An error occured while handling a client connection:", e);
            }
        }
    }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        Options opts = OptParse.parseArgs(new Options(), "tcp-streamer", argv);
        ServerSocket sock = new ServerSocket();
        SocketAddress addr = new InetSocketAddress(opts.listenAddr, opts.listenPort);
        sock.bind(addr);
        for (;;) {
            log.info("Waiting at {} for client to connect...", addr);
            Socket client = sock.accept();
            log.info("Got client connection, sending file {}", opts.file);
            new Thread(new ConnectionHandler(client, opts.file, opts.skip, opts.maxBps)).start();
        }
    }
}