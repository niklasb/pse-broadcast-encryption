package cryptocast.server.programs;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;

import com.beust.jcommander.Parameter;
import com.google.common.io.ByteStreams;

import cryptocast.comm.StreamMessageInChannel;
import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
import cryptocast.server.OptParse;
import cryptocast.util.FileUtils;
import cryptocast.util.SerializationUtils;

/**
 * The main method to start the server.
 */
public final class Client {
    static class Options extends OptParse.WithHelp {
        @Parameter(names = { "-t", "--type" }, description = "Type of received data (text, raw, debug)")
        private String type = "text";
       
        @Parameter(names = { "-k", "--keyfile" }, description = "User key file", 
                   required = true)
        private File keyFile;
        
        @Parameter(names = { "-p", "--port" }, description = "Connect port")
        private int connectPort = 21337;

        @Parameter(names = { "-c", "--host" }, description = "Connect host")
        private String connectHost = "127.0.0.1";
        
        @Parameter(names = { "-o", "--outfile" }, description = "Output file")
        private String outfile = null;
    }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        Options opts = OptParse.parseArgs(new Options(), "client", argv);
        
        NPKey<BigInteger, SchnorrGroup> key = 
                SerializationUtils.readFromFile(opts.keyFile);
        Socket sock = new Socket(opts.connectHost, opts.connectPort);
        BroadcastEncryptionClient in =
                new BroadcastEncryptionClient(
                        new StreamMessageInChannel(sock.getInputStream()), 
                        new SchnorrNPClient(key));
        if (opts.type.equals("text")) {
            int received;
            byte[] buffer = new byte[0x1000];
            while ((received = in.read(buffer)) >= 0) {
                System.out.print(new String(buffer, 0, received, "UTF-8"));
            }
        } else if (opts.type.equals("raw")) {
            if (opts.outfile == null) {
                System.err.println("Must specify -o option for type raw!");
                System.exit(1);
            }
            OutputStream out = new FileOutputStream(FileUtils.expandPath(opts.outfile));
            ByteStreams.copy(in, out);
        } else if (opts.type.equals("debug")) {
            int received;
            byte[] buffer = new byte[0x1000];
            while ((received = in.read(buffer)) >= 0) {
                System.out.println(received + " bytes");
            }
        } else {
            System.err.println("Availale types: raw, text");
        }
    }
}