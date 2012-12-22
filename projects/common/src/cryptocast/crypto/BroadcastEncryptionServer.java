package cryptocast.crypto;

import cryptocast.comm.*;

import java.io.IOException;
import java.io.OutputStream;
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
    private BroadcastSchemeUserManager<ID> context;
    private Encryptor<byte[]> enc;
    private MessageOutChannel controlChannel;
    private OutputStream payloadStream;
    OutputCipherControl cipherControl;

    /**
     * Initializes a broadcast encryption server.
     * @param inner The message-based communication channel to send outgoing data to
     * @param context The user management context
     * @param enc The encryption context
     */
    public BroadcastEncryptionServer(BroadcastSchemeUserManager<ID> context,
                                     Encryptor<byte[]> enc,
                                     MessageOutChannel controlChannel,
                                     OutputStream payloadStream,
                                     OutputCipherControl cipherControl) {
        this.context = context;
        this.enc = enc;
        this.controlChannel = controlChannel;
        this.payloadStream = payloadStream;
        this.cipherControl = cipherControl;
    }

    public static <ID> BroadcastEncryptionServer<ID> buildForMessageChannel(
            BroadcastSchemeUserManager<ID> context,
            Encryptor<byte[]> enc,
            MessageOutChannel inner,
            int bufsize,
            int keyBits) {
        MessageOutChannel controlChannel = createTypedChannel((byte)0, inner);
        ControllableCipherOutputStream payloadStream = 
                ControllableCipherOutputStream.setup(
                    new PacketizingOutputStream(
                        createTypedChannel((byte)1, inner),
                        bufsize),
                    keyBits);
        return new BroadcastEncryptionServer<ID>(
                context, enc, controlChannel, payloadStream, payloadStream);
    }
    
    private static MessageOutChannel createTypedChannel(
                               byte typeId, MessageOutChannel inner) {
        return new DecoratingMessageOutChannel(
            inner, 
            new byte[] { typeId }, // prefix (1 byte indicating the type)
            new byte[0]);          // suffix (none)
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
    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        Action action;
        while (null != (action = pendingActions.poll())) {
            if (action == Action.UPDATE_KEY) {
                cipherControl.updateKey();
            }
            controlChannel.sendMessage(
                    enc.encrypt(
                            cipherControl.getKey().getEncoded()));
            cipherControl.reinitializeCipher();
        }
        payloadStream.write(data, offset, len);
    }

    public void scheduleKeyUpdate() {
        pendingActions.add(Action.UPDATE_KEY);
    }

    public void scheduleKeyBroadcast() {
        pendingActions.add(Action.BROADCAST_KEY);
    }

    /**
     * Revokes a user.
     * @param id The identity of the user
     */
    public void revoke(ID id) throws NoMoreRevocationsPossibleError {
        context.revoke(id);
        scheduleKeyUpdate();
    }
    
    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte)b }, 0, 1);
    }
}
