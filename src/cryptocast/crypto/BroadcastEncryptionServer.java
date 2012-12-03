package cryptocast.crypto;

import cryptocast.transport.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * The server side of a broadcast encryption scheme.
 * @param ID The type of the identities
 */
public class BroadcastEncryptionServer<ID> implements OutChannel, Runnable {
    /**
     * Initializes a broadcast encryption server.
     * @param inner The message-based communication channel to send outgoing data to
     * @param context The user management context
     * @param enc The encryption context
     */
    public BroadcastEncryptionServer(MessageOutChannel inner,
                                     BroadcastSchemeUserManager<ID> context,
                                     Encryptor<BigInteger> enc) { }

    /**
     * Run the worker that handles periodic group key broadcasts and sends
     * queued data packages.
     */
    public void run() {}

    /**
     * Send plaintext data to the channel. It will be encryted and broadcasted
     * on the fly.
     * @param data The data to send
     */
    public void send(ByteBuffer data) {}

    /**
     * Revoke a user.
     * @param id The identity of the user
     */
    public void revoke(ID id) {}
}
