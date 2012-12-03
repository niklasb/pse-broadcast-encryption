package broadcastenc.crypto;

import broadcastenc.transport.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;

/** The server side of a broadcast encryption scheme.
 * @param ID The type of the identities
 */
public class BroadcastEncryptionServer<ID> implements OutChannel, Runnable {
    public BroadcastEncryptionServer(MessageOutChannel inner,
                                     BroadcastSchemeServerContext<BigInteger, ID> context) { }

    public void run() {}
    /**
     * Send plaintext data to the channel that will be encryted and broadcasted
     * on the fly.
     * @param data The data to send
     */
    public void send(ByteBuffer data) {}
    /**
     * Revoke a user.
     * @param id The identity of the user
     */
    public void revoke(ID id) {}
    /**
     * @param i The index of the identity to receive
     * @return the identitiy with the given index
     */
    public ID getIdentity(int i) { return null; }
}
