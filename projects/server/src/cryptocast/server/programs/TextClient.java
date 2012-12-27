package cryptocast.server.programs;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.PrivateKey;

import com.google.common.base.Optional;

import cryptocast.comm.StreamMessageInChannel;
import cryptocast.crypto.BroadcastEncryptionClient;
import cryptocast.crypto.DynamicCipherInputStream;
import cryptocast.crypto.naorpinkas.NaorPinkasClient;
import cryptocast.crypto.naorpinkas.NaorPinkasPersonalKey;
import cryptocast.server.*;
import cryptocast.util.SerializationUtils;

/**
 * The main method to start the server.
 */
public final class TextClient {
    private TextClient() { }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: cryptocast-text-client key-file connect-host:connect-port");
            System.exit(2);
        }
        NaorPinkasPersonalKey key = SerializationUtils.readFromFile(new File(args[0]));
        Optional<InetSocketAddress> address = Shell.parseHostPort(args[1], "127.0.0.1");
        if (!address.isPresent()) {
            System.err.println("Invalid host/port combination: " + args[1]);
            System.exit(1);
        }
        Socket sock = new Socket();
        System.out.println("Connecting to server...");
        sock.connect(address.get());
        System.out.println("Setting up encryption client");
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