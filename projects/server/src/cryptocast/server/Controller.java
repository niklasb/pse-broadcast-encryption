package cryptocast.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.SocketAddress;

import cryptocast.crypto.BroadcastEncryptionServer;
import cryptocast.crypto.SchnorrGroup;
import cryptocast.crypto.naorpinkas.NaorPinkasIdentity;
import cryptocast.crypto.naorpinkas.NaorPinkasServer;
import cryptocast.util.SerializationUtils;
import cryptocast.comm.SocketMulticastServer;
import cryptocast.comm.StreamMessageOutChannel;

/** Deals with user-interactions and therefore changes data in the model if necessary.
 * @param <ID> The type of the user identities
 */
public class Controller {
    private static final int AES_KEY_BITS = 256;

    private ServerData<NaorPinkasIdentity> data;
    private OutputStream out;
    private File databaseFile;

    /**
     * Initializes a new controller with the given arguments.
     * @param data The data managed by this controller.
     * @param shell The operator interface from which this controller gets its input.
     */
    public Controller(ServerData<NaorPinkasIdentity> data, 
                      OutputStream out,
                      File databaseFile) {
        this.data = data;
        this.out = out;
        this.databaseFile = databaseFile;
    }

    public static Controller start(File databaseFile, 
                                   SocketAddress bindAddress)
                throws IOException, ClassNotFoundException {
        ServerData<NaorPinkasIdentity> data;
        if (databaseFile.exists()) {
            data = SerializationUtils.readFromFile(databaseFile);
        } else {
            data = createNewData(0);
        }
        ServerSocket socket = new ServerSocket();
        socket.bind(bindAddress);
        OutputStream out = BroadcastEncryptionServer.start(
                data.users, data.enc, AES_KEY_BITS,
                        new StreamMessageOutChannel(
                                new SocketMulticastServer(socket, null)));
        return new Controller(data, out, databaseFile);
    }
    
    private static ServerData<NaorPinkasIdentity> createNewData(int t) {
        NaorPinkasServer server = NaorPinkasServer.generate(t, SchnorrGroup.getP1024Q160());
        return new ServerData<NaorPinkasIdentity>(server, server, server);
    }
    
    public void saveDatabase() throws IOException {
        SerializationUtils.writeToFile(databaseFile, data);
    }

    public ServerData<NaorPinkasIdentity> getModel() {
        return data;
    }

    public void reinitializeCrypto(int t) {
    }

    /**
     * Starts the data stream.
     * @param data The file from which the data is read.
     */
    public void stream(InputStream in) throws IOException {
        int received;
        byte[] buffer = new byte[4096];
        while ((received = in.read(buffer)) >= 0) {
            out.write(buffer, 0, received);
        }
    }
}
