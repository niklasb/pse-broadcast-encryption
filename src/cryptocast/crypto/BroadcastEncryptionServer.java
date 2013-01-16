package cryptocast.crypto;

import cryptocast.comm.*;
import cryptocast.util.CanBeObserved;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * The server side of a broadcast encryption scheme.
 * @param <ID> The type of the identities
 */
public class BroadcastEncryptionServer<ID> extends OutputStream 
                                           implements Runnable, Observer {
    private static final Logger log = LoggerFactory
            .getLogger(BroadcastEncryptionServer.class);
    
    private DynamicCipherOutputStream cipherStream;
    private int intervalMilliseconds;
    private Function<Throwable, Boolean> excHandler;
    
    /**
     * Initializes a broadcast encryption server.
     * 
     * @param context The user management context.
     * @param cipherStream The ciphered input stream.
     * @param intervalMilliseconds The broadcast time interval in millis.
     * @param excHandler Handler callback, which is called if an exception occurs in 
     *                   the key update loop. If it returns <code>true</code>, the 
     *                   exception is ignored, otherwise the loop is exited.
     */
    public BroadcastEncryptionServer(CanBeObserved userManager,
                                     DynamicCipherOutputStream cipherStream,
                                     int intervalMilliseconds,
                                     Function<Throwable, Boolean> excHandler) {
        this.cipherStream = cipherStream;
        this.intervalMilliseconds = intervalMilliseconds;
        this.excHandler = excHandler;
        userManager.addObserver(this);
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
            Function<Throwable, Boolean> excHandler) throws IOException {
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
                log.trace("Broadcasting key");
                broadcastKey();
            } catch (Throwable e) {
                log.trace("Caught exception in key broadcast loop. Calling handler.", e);
                if (!excHandler.apply(e)) {
                    return;
                }
                log.debug("Handler told us to ignore the exception.");
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

    @Override
    public void update(Observable arg0, Object arg1) {
        // the set of revoked users changed, we need a key update!
        try {
            updateKey();
        } catch (Exception e) {
            log.trace("Caught exception during key update. Calling handler.", e);
            excHandler.apply(e);
        }
    }
}