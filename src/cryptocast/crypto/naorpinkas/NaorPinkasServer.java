package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.util.Collection;

/**
 * A server in the Naor-Pinkas broadcast encryption scheme. This server is special in that it knows
 * the entire polynomial and therefore all the private keys of its users. This makes the implementation
 * of the interpolation algorithm a bit more efficient.
 */
public class NaorPinkasServer
      implements BroadcastSchemeUserManager<NaorPinkasIdentity>,
                 BroadcastSchemeKeyManager<NaorPinkasIdentity>,
                 Serializable,
                 Encryptor<BigInteger> {
    /** {@inheritDoc} */
    public ByteBuffer encrypt(BigInteger secret) {return null;}
    /** {@inheritDoc} */
    public NaorPinkasIdentity getIdentity(int i) {return null;}
    /** {@inheritDoc} */
    public void revoke(NaorPinkasIdentity x) {}
    /** {@inheritDoc} */
    public boolean isRevoked(NaorPinkasIdentity id) {return false;}
    /** {@inheritDoc} */
    public NaorPinkasPersonalKey getPersonalKey(NaorPinkasIdentity id) {return null;}
}
