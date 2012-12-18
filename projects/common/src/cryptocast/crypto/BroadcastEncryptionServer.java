package cryptocast.crypto;

import cryptocast.comm.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

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
    private RawMessageOutChannel inner;
    private BroadcastSchemeUserManager<ID> context;
    private Encryptor<BigInteger> enc;
    OutputStream outStream;

    /**
     * Initializes a broadcast encryption server.
     * @param inner The message-based communication channel to send outgoing data to
     * @param context The user management context
     * @param enc The encryption context
     */
    public BroadcastEncryptionServer(RawMessageOutChannel inner,
                                     BroadcastSchemeUserManager<ID> context,
                                     Encryptor<BigInteger> enc) {
        this.inner = inner;
        this.context = context;
        this.enc = enc;
        pendingActions.add(Action.UPDATE_KEY);
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
    public void write(byte[] data, int offset, int len) throws IOException {
        Action action;
        while (null != (action = pendingActions.poll())) {
            if (action == Action.UPDATE_KEY) {
                this.key = new Key();
            }
            sendTypedMessage(0, enc.encrypt(key));
            updateCipherStream();
        }
        outStream.write(data, offset, len);
    }

    private void sendTypedMessage(byte type, byte[] message) throws IOException {
        byte[] buffer = new byte[message.length + 1];
        System.arraycopy(message, 0, buffer, 1, message.length);
        buffer[0] = type;
        inner.sendMessage(buffer);
    }
    
    private void updateCipherStream() {
        Cipher c = new Cipher(key);
        outStream = new CipherOutputStream(
            new PacketizingOutputStream(inner, 4096 * 1024), c);
    }

    /**
     * Revokes a user.
     * @param id The identity of the user
     */
    public void revoke(ID id) {
        context.revoke(id);
        pendingActions.add(Action.UPDATE_KEY);
    }

    @Override
    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }
    
    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte)b }, 0, 1);
    }
}
