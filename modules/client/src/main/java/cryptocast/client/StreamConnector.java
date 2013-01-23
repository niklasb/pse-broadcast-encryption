package cryptocast.client;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;

import cryptocast.comm.StreamMessageInChannel;
import cryptocast.crypto.BroadcastEncryptionClient;
import cryptocast.crypto.EllipticCurveGroup;
import cryptocast.crypto.EllipticCurveOverFp;
import cryptocast.crypto.SchnorrGroup;
import cryptocast.crypto.naorpinkas.ECNPClient;
import cryptocast.crypto.naorpinkas.NPKey;
import cryptocast.crypto.naorpinkas.SchnorrNPClient;
import cryptocast.util.SerializationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class does StreamViewerActivity's work to ensure that the activity is displayed
 * correctly.
 * @author Christoph
 *
 */
public class StreamConnector implements Runnable {
    private Socket sock;
    private InetSocketAddress connectAddr;
    private File keyFile;
    private RawStreamMediaPlayer player;
    private StreamViewerActivity streamViewerActivity;
    private static final Logger log = LoggerFactory
            .getLogger(StreamConnector.class);
    
    /**
     * Creates a SocketConnector with the given socket.
     * @param socket the socket to connect
     * @param connectAddr 
     * @param player 
     * @param streamViewerActivity 
     */
    public StreamConnector(Socket socket, InetSocketAddress connectAddr, File keyFile, 
            RawStreamMediaPlayer player, StreamViewerActivity streamViewerActivity) {
        this.sock = socket;
        this.connectAddr = connectAddr;
        this.keyFile = keyFile;
        this.player = player;
        this.streamViewerActivity = streamViewerActivity;
        streamViewerActivity.setStatusText("Initializing..."); 
    }

    @Override
    public void run(){
        connectToStream();
    }
    
    /**
     * Stops the connector.
     */
    public void stop() {
        try { sock.close(); } catch (Throwable e) {}
    }
    
    private void connectToStream() {
        log.debug("Connecting to {}", connectAddr);
        streamViewerActivity.setStatusText("Connecting to server...");

        sock = new Socket();

        try {
            sock.connect(connectAddr, 5000);
        } catch (Exception e) {
            log.error("Could not connect to target server", e);
            streamViewerActivity.handleError("Could not connect to server!");
            return;
        }
        log.debug("Connected to {}", connectAddr);
        streamViewerActivity.setStatusText("Connected to server...");

        receiveData();
    }

    private void receiveData() {
        log.debug("Assuming that the server uses the NP scheme base on schnorr groups."
                + " Erasure makes it hard to extract the actual type from the key file");
//        NPKey<EllipticCurveOverFp.Point, 
//              EllipticCurveGroup<BigInteger, 
//                                 EllipticCurveOverFp.Point, 
//                                 EllipticCurveOverFp>> key;
        NPKey<BigInteger, SchnorrGroup> key;
        try {
            key = SerializationUtils.readFromFile(keyFile);
        } catch (Exception e) {
            log.error("Could not load key from file: ", e);
            streamViewerActivity.handleError("Invalid key file!");
            return;
        }

        InputStream in;
        try {
            in = new BroadcastEncryptionClient(
                        new StreamMessageInChannel(sock.getInputStream()), 
                        new SchnorrNPClient(key));
                        //new ECNPClient(key));
            log.debug("Waiting for first byte");
            streamViewerActivity.setStatusText("Decrypting the stream...");
            in.read();
        } catch (Exception e) {
            log.error("Error while receiving the first byte", e);
            streamViewerActivity.handleError();
            return;
        }
        log.debug("Preparing media player...");
        streamViewerActivity.setStatusText("Buffering...");
        try {
            player.setRawDataSource(in, "audio/mpeg");
            player.prepare();
        } catch (Exception e) {
            log.error("Error while preparing the media player", e);
            streamViewerActivity.handleError();
            return;
        }
    }
}
