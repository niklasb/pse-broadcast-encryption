package cryptocast.server.programs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import cryptocast.server.OptParse;
import cryptocast.comm.SimpleHttpStreamServer;

/**
 * The main method to start the server.
 */
public final class HttpStreamer {
    private static final Logger log = LoggerFactory
            .getLogger(HttpStreamer.class);
    
    static class Options extends OptParse.WithHelp {
        @Parameter(names = { "-p", "--port" }, description = "Bind port")
        private int listenPort = 21337;

        @Parameter(names = { "-b", "--listen-address"}, description = "Bind address")
        private String listenAddr = "127.0.0.1";
        
        @Parameter(required = true, names = { "-f", "--file" }, description = "The file to stream")
        private File file;
        
        @Parameter(names = { "-s", "--skip" }, description = "Number of bytes to skip")
        private int skip = 0;
    }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        Options opts = OptParse.parseArgs(new Options(), "http-streamer", argv);
        SocketAddress addr = new InetSocketAddress(opts.listenAddr, opts.listenPort);
        InputStream fis = new FileInputStream(opts.file);
        log.debug("Skipping {} bytes", opts.skip);
        for (int i = 0; i < opts.skip; ++i) {
            fis.read();
        }
        SimpleHttpStreamServer server = new SimpleHttpStreamServer(fis, addr, "audio/mpeg", 0x10000);
        new Thread(server).start();
    }
}