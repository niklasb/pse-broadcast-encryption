package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import static com.google.common.base.Preconditions.*;

/**
 * A server in the Naor-Pinkas broadcast encryption scheme. It knows
 * the entire polynomial and therefore all the private keys of its users. This makes the implementation
 * of the interpolation algorithm a bit more efficient.
 */
public class NaorPinkasServer
      implements BroadcastSchemeUserManager<NaorPinkasIdentity>,
                 BroadcastSchemeKeyManager<NaorPinkasIdentity>,
                 Serializable,
                 Encryptor<byte[]> {
    private int t;
    private ModularExponentiationGroup group;
    
    /**
     * Encrypts a secret.
     * @param secret the secret
     * @return The cipher text
     */
    public byte[] encrypt(byte[] secret) {
        BigInteger x = new BigInteger(1, secret);
        checkArgument(x.compareTo(group.getP()) < 0, "Secret is too large to encrypt");
        return null;
    }

    /**
     * @param i An index
     * @return The identity with the given index
     */
    public NaorPinkasIdentity getIdentity(int i) {return null;}
    /**
     * Revokes a user.
     * @param id The identity of the user
     */
    public void revoke(NaorPinkasIdentity x) {}
    /**
     * @param id The identity of the user
     * @return Whether the user is revoked.
     */
    public boolean isRevoked(NaorPinkasIdentity id) {return false;}
    /**
     * @param id The identity to look up
     * @return The private key of the user
     */
    public NaorPinkasPersonalKey getPersonalKey(NaorPinkasIdentity id) {return null;}
}
