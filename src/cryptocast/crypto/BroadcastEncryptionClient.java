package cryptocast.crypto;

import cryptocast.comm.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * The client side of a broadcast encryption scheme.
 */
public class BroadcastEncryptionClient implements InChannel {
    /**
     * Initializes a broadcast encryption client.
     * @param inner The message-based underlying communication channel.
     * @param dec The decryption context
     */
    public BroadcastEncryptionClient(MessageInChannel inner,
                                     Decryptor<BigInteger> dec) { }

    /**
     * Receive data from the channel. It is decrypted on the fly.
     * @param size amount of bytes to receive
     */
    public ByteBuffer recv(int size) { return null; }
}
