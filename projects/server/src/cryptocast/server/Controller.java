package cryptocast.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.List;

import com.google.common.base.Optional;

import cryptocast.crypto.BroadcastEncryptionServer;
import cryptocast.crypto.SchnorrGroup;
import cryptocast.crypto.naorpinkas.NaorPinkasIdentity;
import cryptocast.crypto.naorpinkas.NaorPinkasPersonalKey;
import cryptocast.crypto.naorpinkas.NaorPinkasServer;
import cryptocast.util.SerializationUtils;
import cryptocast.comm.MessageOutChannel;
import cryptocast.comm.SocketMulticastServer;
import cryptocast.comm.StreamMessageOutChannel;

/** Deals with user-interactions and therefore changes data in the model if necessary.
 * @param <ID> The type of the user identities
 */
public class Controller {
    private static final int AES_KEY_BITS = 256;

    private NaorPinkasServerData data;
    private MessageOutChannel rawOut;
    private File databaseFile;
    private BroadcastEncryptionServer<NaorPinkasIdentity> encServer;

    /**
     * Initializes a new controller with the given arguments.
     * @param data The data managed by this controller.
     * @param shell The operator interface from which this controller gets its input.
     */
    private Controller(NaorPinkasServerData data,
                       File databaseFile,
                       MessageOutChannel rawOut,
                       BroadcastEncryptionServer<NaorPinkasIdentity> encServer) {
        this.data = data;
        this.rawOut = rawOut;
        this.databaseFile = databaseFile;
        this.encServer = encServer;
    }

    public static Controller start(File databaseFile, 
                                   SocketAddress bindAddress)
                throws IOException, ClassNotFoundException {
        NaorPinkasServerData data;
        if (databaseFile.exists()) {
            data = SerializationUtils.readFromFile(databaseFile);
        } else {
            data = createNewData(0);
        }
        ServerSocket socket = new ServerSocket();
        socket.bind(bindAddress);
        MessageOutChannel rawOut = new StreamMessageOutChannel(
                                      new SocketMulticastServer(socket, null));
        return new Controller(data, databaseFile, rawOut, 
                    startBroadcastEncryptionServer(data, rawOut));
    }

    private static NaorPinkasServerData createNewData(int t) {
        return new NaorPinkasServerData(
                NaorPinkasServer.generate(t, SchnorrGroup.getP1024Q160()));
    }

    public void saveUserKeys(File dir, List<User<NaorPinkasIdentity>> users) 
                                  throws IOException {
        for (User<NaorPinkasIdentity> user : users) {
            File keyFile = new File(dir.getAbsolutePath() + "/" + user.getName() + ".key");
            Optional<NaorPinkasPersonalKey> mKey = 
                    data.npServer.getPersonalKey(user.getIdentity());
            assert mKey.isPresent();
            SerializationUtils.writeToFile(keyFile, mKey.get());
        }
    }
    
    public void saveDatabase() throws IOException {
        SerializationUtils.writeToFile(databaseFile, data);
    }

    public ServerData<NaorPinkasIdentity> getModel() {
        return data;
    }

    public void reinitializeCrypto(int t) throws IOException {
        data = createNewData(t);
        encServer = startBroadcastEncryptionServer(data, rawOut);
    }

    private static BroadcastEncryptionServer<NaorPinkasIdentity> 
    startBroadcastEncryptionServer(
            NaorPinkasServerData data, MessageOutChannel rawOut)
                    throws IOException {
        return BroadcastEncryptionServer.start(
                data.userManager, data.npServer, AES_KEY_BITS, rawOut);
    }

    public int getT() {
        return data.npServer.getT();
    }
    
    /**
     * Starts the data stream.
     * @param data The file from which the data is read.
     */
    public void stream(InputStream in) throws IOException {
        int received;
        byte[] buffer = new byte[4096];
        while ((received = in.read(buffer)) >= 0) {
            encServer.write(buffer, 0, received);
        }
    }
}