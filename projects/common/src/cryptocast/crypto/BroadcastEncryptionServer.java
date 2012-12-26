package cryptocast.crypto;

import cryptocast.comm.*;

import java.io.FilterOutputStream;
import java.io.IOException;

/**
 * The server side of a broadcast encryption scheme
 * @param <ID> The type of the identities
 */
public class BroadcastEncryptionServer<ID> extends FilterOutputStream {
    private BroadcastSchemeUserManager<ID> context;
    private DynamicCipherOutputStream cipherStream;
    
    /**
     * Initializes a broadcast encryption server.
     * @param inner The message-based communication channel to send outgoing data to
     * @param context The user management context
     * @param enc The encryption context
     */
    public BroadcastEncryptionServer(BroadcastSchemeUserManager<ID> context,
                                     DynamicCipherOutputStream cipherStream) {
        super(cipherStream);
        this.cipherStream = cipherStream;
        this.context = context;
    }

    public static <ID> BroadcastEncryptionServer<ID> start(
            BroadcastSchemeUserManager<ID> context,
            Encryptor<byte[]> enc,
            int symmetricKeyBits,
            MessageOutChannel inner) throws IOException {
        return new BroadcastEncryptionServer<ID>(context, 
                DynamicCipherOutputStream.start(inner, symmetricKeyBits, enc));
    }

    /**
     * Runs the worker that handles periodic group key broadcasts and sends
     * queued data packages.
     */
    public void run() {}
    
    public void updateKey() throws IOException {
        cipherStream.updateKey();
    }

    public void broadcastKey() throws IOException {
        cipherStream.reinitializeCipher();
    }

    /**
     * Revokes a user.
     * @param id The identity of the user
     */
    public void revoke(ID id) throws NoMoreRevocationsPossibleError, IOException {
        if (context.revoke(id)) {
            updateKey();
        }
    }
    
    public void unrevoke(ID id) throws IOException {
        if (context.unrevoke(id)) {
            updateKey();
        }
    }
}