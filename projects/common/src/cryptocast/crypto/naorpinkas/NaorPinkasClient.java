package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.math.BigInteger;

/**
 * A client in the Naor-Pinkas broadcast encryption scheme.
 */
public class NaorPinkasClient implements Decryptor<byte[]> {
    /**
    * Initializes a Naor-Pinkas broadcast client.
    * @param key The personal key used to reconstruct a secret from the stream.
    */
    public NaorPinkasClient(NaorPinkasPersonalKey key) { }

    /**
      * Decrypts a secret.
      * @param cipher The encrypted secret
      * @return The decrypted secret
      */
    public BigInteger decrypt(byte[] cipher) { return null; }
}
