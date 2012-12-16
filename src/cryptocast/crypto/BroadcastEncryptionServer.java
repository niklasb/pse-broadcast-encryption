package cryptocast.crypto;

import cryptocast.comm.*;
import java.math.BigInteger;
import java.util.Collection;

/**
 * The server side of a broadcast encryption scheme
 * @param <ID> The type of the identities
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
     * Runs the worker that handles periodic group key broadcasts and sends
     * queued data packages.
     */
    public void run() {}

    /**
     * Sends plaintext data to the channel. It will be encryted and broadcasted
     * on the fly.
     * @param data The data to send
     */
    public void send(byte[] data) {}

    /**
     * Revokes a user.
     * @param id The identity of the user
     */
    public void revoke(ID id) {}
}
