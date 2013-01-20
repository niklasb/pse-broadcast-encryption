package cryptocast.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.tritonus.share.sampled.TAudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
import cryptocast.util.ByteUtils;
import cryptocast.util.SerializationUtils;
import cryptocast.comm.*;

/**
 * Deals with user-interactions and therefore changes data in the model if
 * necessary.
 */
public class Controller implements Observer {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    
    // don't use AES-256 because that would require all users 
    // to have the Unlimited Strength Jurisdiction Policy Files installed.
    // Also, AES-128 seems to be more secure
    private static final int AES_KEY_BITS = 128;
    
    private NaorPinkasServerData data;
    private MessageOutChannel rawOut;
    private File databaseFile;
    private BroadcastEncryptionServer<NPIdentity> encServer;
    private SocketAddress listenAddr;
    private int keyBroadcastIntervalSecs;
    private SwitchableInputStream switchableInput;
    private SwitchableOutputStream switchableOutput;
    NPServerFactory serverFactory;
    
    private static Function<Throwable, Boolean> fatalExceptionHandler =
            new Function<Throwable, Boolean>() {
                @Override
                public Boolean apply(Throwable e) {
                    log.error("A fatal error occured in a background thread, exiting!", e);
                    System.exit(1);
                    return false; // just for the lulz
                }
            };
            
    private Controller(NaorPinkasServerData data, File databaseFile,
            MessageOutChannel rawOut,
            BroadcastEncryptionServer<NPIdentity> encServer,
            SocketAddress listenAddr, int keyBroadcastIntervalSecs,
            SwitchableInputStream switchableInput,
            SwitchableOutputStream switchableOutput,
            NPServerFactory serverFactory) {
        this.data = data;
        data.addObserver(this);
        this.rawOut = rawOut;
        this.databaseFile = databaseFile;
        this.encServer = encServer;
        this.listenAddr = listenAddr;
        this.keyBroadcastIntervalSecs = keyBroadcastIntervalSecs;
        this.switchableInput = switchableInput;
        this.switchableOutput = switchableOutput;
        this.serverFactory = serverFactory;
    }

    /**
     * Creates a Controller with the given parameters
     * 
     * @param databaseFile The database file.
     * @param listenAddr The socket address to bind to.
     * @param keyBroadcastIntervalSecs the broadcast interval in seconds
     * @return The controller
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Controller start(File databaseFile, SocketAddress listenAddr,
                                   int keyBroadcastIntervalSecs,
                                   NPServerFactory serverFactory) 
                   throws IOException, ClassNotFoundException {
        NaorPinkasServerData data;
        if (databaseFile.exists()) {
            data = SerializationUtils.readFromFile(databaseFile);
            data.initAfterDeserialization();
        } else {
            data = createNewData(0, serverFactory);
        }
        ServerSocket socket = new ServerSocket();
        socket.bind(listenAddr);
        ServerMultiMessageOutChannel multicastServer = new ServerMultiMessageOutChannel(
                socket, fatalExceptionHandler);
        new Thread(multicastServer).start();
        
        final BroadcastEncryptionServer<NPIdentity> broadcastServer = 
                startBroadcastEncryptionServer(data, multicastServer, keyBroadcastIntervalSecs);
        final SwitchableInputStream input = new SwitchableInputStream();
        final SwitchableOutputStream output = new SwitchableOutputStream();
        output.switchOutput(broadcastServer);
        new Thread(new Runnable() {
            public void run() {
                try {
                    StreamUtils.copyInterruptable(input, output, 0x4000);
                } catch (Exception e) {
                    fatalExceptionHandler.apply(e);
                }
            }
        }).start();
        return new Controller(data, databaseFile, multicastServer,
                broadcastServer, listenAddr,
                keyBroadcastIntervalSecs, input, output, serverFactory);
    }

    private static NaorPinkasServerData createNewData(int t, NPServerFactory serverFactory) {
        return new NaorPinkasServerData(serverFactory.construct(t));
    }

    /**
     * Saves the users's personal keys in a keyfile at the given directory.
     * 
     * @param dir The directory to save the keyfiles in.
     * @param users The users who their personal keys will be saved.
     * @throws IOException
     */
    public void saveUserKeys(File dir, Set<User<NPIdentity>> users)
            throws IOException {
        for (User<NPIdentity> user : users) {
            File keyFile = new File(dir.getAbsolutePath() + "/"
                    + user.getName() + ".key");
            Optional<? extends PrivateKey> mKey = data.npServer
                    .getPersonalKey(user.getIdentity());
            assert mKey.isPresent();
            SerializationUtils.writeToFile(keyFile, mKey.get());
        }
    }

    /**
     * Saves the database.
     * 
     * @throws IOException
     */
    public void saveDatabase() throws IOException {
        log.debug("Saving database to {}", databaseFile);
        SerializationUtils.writeToFile(databaseFile, data);
    }

    /**
     * @return the data.
     */
    public NaorPinkasServerData getModel() {
        return data;
    }
    
    /**
     * Reinitializes the cryptography. 
     * All data of the current session will be lost forever!
     * 
     * @param t the size of the polynomial.
     * @throws IOException
     */
    public void reinitializeCrypto(int t) 
            throws IOException {
        data = createNewData(t, serverFactory);
        data.addObserver(this);
        encServer = startBroadcastEncryptionServer(
                data, rawOut, keyBroadcastIntervalSecs);
        switchableOutput.switchOutput(encServer);
        saveDatabase();
    }

    /**
     * Streams the data.
     * 
     * @param in The input stream.
     * @throws IOException
     */
    public void stream(InputStream in) throws IOException {
        switchableInput.switchInput(in);
    }

    /**
     * Streams the data.
     * 
     * @param in The input stream.
     * @param maxBytesPerSec Maximum bytes per second.
     * @throws IOException
     */
    public void stream(InputStream in, long maxBytesPerSec) throws IOException {
        stream(new ThrottledInputStream(in, maxBytesPerSec));
    }

    private static BroadcastEncryptionServer<NPIdentity> startBroadcastEncryptionServer(
            NaorPinkasServerData data, MessageOutChannel rawOut,
            int keyBroadcastIntervalSecs) throws IOException {
        BroadcastEncryptionServer<NPIdentity> server = BroadcastEncryptionServer
                .start(data.userManager, data.npServer, AES_KEY_BITS, rawOut,
                       keyBroadcastIntervalSecs * 1000, // update every 15 seconds
                       fatalExceptionHandler);
        new Thread(server).start();
        return server;
    }
    
    /**
     * @return The database file.
     */
    public File getDatabaseFile() {
        return databaseFile;
    }

    /**
     * @return The size of the polynomial.
     */
    public int getT() {
        return data.npServer.getT();
    }

    /**
     * @return The socket listening address to bind to.
     */
    public SocketAddress getListenAddress() {
        return listenAddr;
    }

    /**
     * Streams simple text.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void streamSampleText() throws IOException, InterruptedException {
        int counter = 0;
        for (;;) {
            encServer.write(ByteUtils.encodeUtf8((counter++) + "\n"));
            encServer.flush();
            Thread.sleep(1000);
        }
    }

    /**
     * Streams audio.
     * 
     * @param file The audio file from which the data is read.
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void streamAudio(File file) throws IOException,
            UnsupportedAudioFileException {
        AudioFormat baseFormat = AudioSystem.getAudioFileFormat(file)
                .getFormat();
        int bitrate = -1;
        boolean vbr = false;
        if (baseFormat instanceof TAudioFormat) {
            Map<String, Object> properties = ((TAudioFormat) baseFormat)
                    .properties();
            if (properties.containsKey("bitrate")) {
                bitrate = (Integer) properties.get("bitrate");
            }
            if (properties.containsKey("vbr")) {
                vbr = (Boolean) properties.get("vbr");
            }
        }
        log.info("Bitrate: {}", bitrate);
        if (bitrate < 0 || vbr) {
            throw new IOException(
                    "Could not figure out the bitrate of the file. "
                            + "Variable bitrates are not supported!");
        }
        InputStream in = new FileInputStream(file);
        stream(in, bitrate / 8);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            saveDatabase();
        } catch (Exception e) {
            log.error("Error while saving database", e);
        }
    }

}
