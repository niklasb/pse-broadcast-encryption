package cryptocast.server.programs;

import java.io.File;
import java.net.Socket;

import com.beust.jcommander.Parameter;

import cryptocast.comm.StreamMessageInChannel;
import cryptocast.crypto.BroadcastEncryptionClient;
import cryptocast.crypto.naorpinkas.*;
import cryptocast.server.OptParse;
import cryptocast.util.SerializationUtils;

/**
 * The main method to start the server.
 */
public final class TextClient {
    static class Options extends OptParse.WithHelp {        
        @Parameter(names = { "-k", "--keyfile" }, description = "User key file", 
                   required = true)
        private File keyFile;
        
        @Parameter(names = { "-p", "--port" }, description = "Connect port")
        private int connectPort = 21337;

        @Parameter(names = { "-c", "--host"}, description = "Connect host")
        private String connectHost = "127.0.0.1";
    }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        Options opts = OptParse.parseArgs(new Options(), "text-client", argv);
        
        NaorPinkasPersonalKey key = SerializationUtils.readFromFile(opts.keyFile);
        Socket sock = new Socket(opts.connectHost, opts.connectPort);
        BroadcastEncryptionClient in =
                new BroadcastEncryptionClient(
                        new StreamMessageInChannel(sock.getInputStream()), 
                        new NaorPinkasClient(key));
        int received;
        byte[] buffer = new byte[4096];
        while ((received = in.read(buffer)) >= 0) {
            System.out.print(new String(buffer, 0, received, "UTF-8"));
        }
    }
}