package cryptocast.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.List;

import com.google.common.base.Optional;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
import cryptocast.util.ByteUtils;
import cryptocast.util.SerializationUtils;
import cryptocast.comm.*;

/** Deals with user-interactions and therefore changes data in the model if necessary.
 * @param <ID> The type of the user identities
 */
public class Controller {
    private static final int AES_KEY_BITS = 256;
    private static final int KEY_BROADCAST_INTERVAL_SEC = 5;
    
    private NaorPinkasServerData data;
    private MessageOutChannel rawOut;
    private File databaseFile;
    private BroadcastEncryptionServer<NaorPinkasIdentity> encServer;
    private SocketAddress listenAddr;

    /**
     * Initializes a new controller with the given arguments.
     * @param data The data managed by this controller.
     * @param shell The operator interface from which this controller gets its input.
     */
    private Controller(NaorPinkasServerData data,
                       File databaseFile,
                       MessageOutChannel rawOut,
                       BroadcastEncryptionServer<NaorPinkasIdentity> encServer,
                       SocketAddress listenAddr) {
        this.data = data;
        this.rawOut = rawOut;
        this.databaseFile = databaseFile;
        this.encServer = encServer;
        this.listenAddr = listenAddr;
    }

    public static Controller start(File databaseFile, 
                                   SocketAddress listenAddr)
                throws IOException, ClassNotFoundException {
        NaorPinkasServerData data;
        if (databaseFile.exists()) {
            data = SerializationUtils.readFromFile(databaseFile);
        } else {
            data = createNewData(0);
        }
        ServerSocket socket = new ServerSocket();
        socket.bind(listenAddr);
        SocketMulticastServer multicastServer = 
                new SocketMulticastServer(socket, null);
        new Thread(multicastServer).start();
        MessageOutChannel rawOut = 
                new StreamMessageOutChannel(multicastServer);
        return new Controller(data, databaseFile, rawOut, 
                    startBroadcastEncryptionServer(data, rawOut),
                    listenAddr);
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
        BroadcastEncryptionServer<NaorPinkasIdentity> server = 
                BroadcastEncryptionServer.start(
                        data.userManager, data.npServer, AES_KEY_BITS, rawOut,
                        KEY_BROADCAST_INTERVAL_SEC * 1000, // update every 15 seconds
                        null);
        new Thread(server).start();
        return server;
    }

    public File getDatabaseFile() {
        return databaseFile;
    }
    
    public int getT() {
        return data.npServer.getT();
    }
    
    public SocketAddress getListenAddress() {
        return listenAddr;
    }
    
    public void streamSampleText() throws IOException, InterruptedException {
        int counter = 0;
        for (;;) {
            encServer.write(ByteUtils.encodeUtf8((counter++) + "\n"));
            encServer.flush();
            Thread.sleep(1000);
        }
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
            encServer.flush();
        }
    }
}