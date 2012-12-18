package cryptocast.crypto;

import cryptocast.comm.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The server side of a broadcast encryption scheme
 * @param <ID> The type of the identities
 */
public class BroadcastEncryptionServer<ID> extends OutputStream {
    enum Action {
        BROADCAST_KEY,
        UPDATE_KEY,
    }

    ConcurrentLinkedQueue<Action> pendingActions = new ConcurrentLinkedQueue<Action>();
    private MessageOutChannel inner;
    private BroadcastSchemeUserManager<ID> context;
    private Encryptor<BigInteger> enc;

    /**
     * Initializes a broadcast encryption server.
     * @param inner The message-based communication channel to send outgoing data to
     * @param context The user management context
     * @param enc The encryption context
     */
    public BroadcastEncryptionServer(MessageOutChannel inner,
                                     BroadcastSchemeUserManager<ID> context,
                                     Encryptor<BigInteger> enc) {
        this.inner = inner;
        this.context = context;
        this.enc = enc;
    }

    /**
     * Runs the worker that handles periodic group key broadcasts and sends
     * queued data packages.
     */
    public void run() {}

    /**
     * Sends plaintext data to the channel. It will be encryted and broadcasted
     * on the fly.
     * @param data The data to send
     */
    public void write(byte[] data, int offset, int len) {
        Action action;
        while (null != (action = pendingActions.poll())) {
            if (action == Action.UPDATE_KEY) {
                
            }
        }
    }

    /**
     * Revokes a user.
     * @param id The identity of the user
     */
    public void revoke(ID id) {}

    @Override
    public void write(int b) throws IOException {
        // TODO Auto-generated method stub
    }
}
