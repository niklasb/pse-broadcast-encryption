package cryptocast.crypto;

import cryptocast.comm.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * The client side of a broadcast encryption scheme.
 */
public class BroadcastEncryptionClient extends InChannel {
    /**
     * Initializes a broadcast encryption client.
     * @param inner The message-based underlying communication channel.
     * @param dec The decryption context
     */
    public BroadcastEncryptionClient(MessageInChannel inner,
                                     Decryptor<BigInteger> dec) { }

    /**
     * Receives data. Can read less than the remaining number of bytes.
     * Data will be encrypted on the fly.
     * 
     * @param buffer The target buffer
     * @return The amount of bytes read
     */
    @Override
    public int recv(ByteBuffer buffer) { return 0; }
}
