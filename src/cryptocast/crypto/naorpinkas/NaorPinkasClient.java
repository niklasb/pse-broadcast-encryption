package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class NaorPinkasClient implements Decryptor<BigInteger> {
  /**
   * Initializes a Naor-Pinkas broadcast client
   * @param key The personal key used to reconstruct a secret from the stream
   */
  public NaorPinkasClient(NaorPinkasPersonalKey key) { }

  /** {@inheritDoc} */
  public BigInteger decrypt(ByteBuffer cipher) { return null; }
}
