package cryptocast.crypto;

import cryptocast.comm.*;
import cryptocast.util.Callback;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The server side of a broadcast encryption scheme.
 * @param <ID> The type of the identities
 */
public class BroadcastEncryptionServer<ID> extends OutputStream
                                           implements Runnable {
    private static final Logger log = LoggerFactory
            .getLogger(BroadcastEncryptionServer.class);
    
    private BroadcastSchemeUserManager<ID> context;
    private DynamicCipherOutputStream cipherStream;
    private int intervalMilliseconds;
    private Callback<Throwable> excHandler;
    
    /**
     * Initializes a broadcast encryption server.
     * 
     * @param context The user management context.
     * @param cipherStream The ciphered input stream.
     * @param intervalMilliseconds The broadcast time interval in millis.
     * @param excHandler Handler callback.
     */
    public BroadcastEncryptionServer(BroadcastSchemeUserManager<ID> context,
                                     DynamicCipherOutputStream cipherStream,
                                     int intervalMilliseconds,
                                     Callback<Throwable> excHandler) {
        this.cipherStream = cipherStream;
        this.context = context;
        this.intervalMilliseconds = intervalMilliseconds;
        this.excHandler = excHandler;
    }

    /**
     * Returns a broadcast encyption server with the given values.
     * 
     * @param context The user management context.
     * @param enc The encryption context.
     * @param symmetricKeyBits Symmetric key bits used to init a key generator for a certain size.
     * @param inner The message-based communication channel to send outgoing data to.
     * @param intervalMilliseconds The broadcast time interval in millis.
     * @param excHandler The exceptions handler callback.
     * @return A broadcast encyption server with the given values.
     * @throws IOException
     */
    public static <ID> BroadcastEncryptionServer<ID> start(
            BroadcastSchemeUserManager<ID> context,
            Encryptor<byte[]> enc,
            int symmetricKeyBits,
            MessageOutChannel inner,
            int intervalMilliseconds,
            Callback<Throwable> excHandler) throws IOException {
        return new BroadcastEncryptionServer<ID>(context, 
                DynamicCipherOutputStream.start(inner, symmetricKeyBits, enc),
                intervalMilliseconds,
                excHandler);
    }

    /**
     * Runs the worker that handles periodic group key broadcasts and sends
     * queued data packages.
     */
    public void run() {
        for(;;) {
            try {
                Thread.sleep(intervalMilliseconds);
            } catch (InterruptedException e) {
                return;
            }
            try {
                broadcastKey();
            } catch (Exception e) {
                excHandler.handle(e);
            }
        }
    }
    
    /**
     * Updates the key.
     * @throws IOException
     */
    public synchronized void updateKey() throws IOException {
        cipherStream.updateKey();
    }

    /**
     * Broadcasts the key.
     * @throws IOException
     */
    public synchronized void broadcastKey() throws IOException {
        cipherStream.reinitializeCipher();
    }

    /**
     * Revokes a user.
     * 
     * @param id The identity of the user.
     * @throws NoMoreRevocationsPossibleError
     * @throws IOException
     */
    public synchronized void revoke(ID id) throws NoMoreRevocationsPossibleError, IOException {
        if (context.revoke(id)) {
            updateKey();
        }
    }
    
    /**
     * Authorizes a user.
     * 
     * @param id The identity of the user.
     * @throws IOException
     */
    public synchronized void unrevoke(ID id) throws IOException {
        if (context.unrevoke(id)) {
            updateKey();
        }
    }
    
    @Override
    public synchronized void close() throws IOException  {
        cipherStream.close();
    }
    
    @Override
    public synchronized void flush() throws IOException {
        cipherStream.flush();
    }
    
    @Override
    public synchronized void write(int b) throws IOException {
        cipherStream.write(b);
    }
    
    @Override
    public synchronized void write(byte[] buf, int offset, int len) throws IOException {
        cipherStream.write(buf, offset, len);
    }
}