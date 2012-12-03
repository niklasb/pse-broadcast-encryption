package broadcastenc.crypto;

import broadcastenc.transport.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;

/** The server side of a broadcast encryption scheme.
 * @param ID The type of the identities
 */
public class BroadcastEncryptionClient implements InChannel {
    public BroadcastEncryptionClient(MessageInChannel inner,
                                     BroadcastSchemeClientContext<BigInteger> context) { }

    /**
     * Receive plaintext data from the channel.
     * @param size amount of bytes to receive
     */
    public ByteBuffer recv(int size) { return null; }
}
