package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import cryptocast.util.Generator;
import cryptocast.util.OptimisticGenerator;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.*;

/**
 * A server in the Naor-Pinkas broadcast encryption scheme. It knows
 * the entire polynomial and therefore all the private keys of its users. 
 */
public class NaorPinkasServer
      implements BroadcastSchemeUserManager<NaorPinkasIdentity>,
                 BroadcastSchemeKeyManager<NaorPinkasIdentity>,
                 Serializable,
                 Encryptor<byte[]> {
    private static final long serialVersionUID = -6864326409385317975L;
    
    private int t;
    private ModularExponentiationGroup group;
    private Map<NaorPinkasIdentity, NaorPinkasPersonalKey> userByIdentity =
                 new HashMap<NaorPinkasIdentity, NaorPinkasPersonalKey>();
    private Set<NaorPinkasIdentity> revokedUsers =
                 new HashSet<NaorPinkasIdentity>();
    private Generator<NaorPinkasPersonalKey> keyGen;
    
    private static SecureRandom rnd = new SecureRandom();

    private NaorPinkasServer(int t, ModularExponentiationGroup group,
                             Generator<NaorPinkasPersonalKey> keyGen) {
        this.t = t;
        this.group = group;
        this.keyGen = keyGen;
    }
    
    public NaorPinkasServer generate(int t, ModularExponentiationGroup group) {
        Polynomial<BigInteger> poly = Polynomial.createRandomPolynomial(rnd, group, t);
        Generator<NaorPinkasPersonalKey> keyGen = 
                new OptimisticGenerator<NaorPinkasPersonalKey>(
                        new NaorPinkasKeyGenerator(
                                t, new SecureRandom(), group, poly));
        return new NaorPinkasServer(t, group, keyGen);
    }

    /**
     * Encrypts a secret.
     * @param secret the secret
     * @return The cipher text
     */
    public byte[] encrypt(byte[] secret) {
        BigInteger x = new BigInteger(1, secret);
        checkArgument(x.compareTo(group.getP()) < 0, "Secret is too large to encrypt");
        NaorPinkasPersonalKey[] keys = new NaorPinkasPersonalKey[t];
        int i = 0;
        int dummies = t - revokedUsers.size();
        while (i < dummies) {
            keys[i++] = getDummyKey(i);
        }
        for (NaorPinkasIdentity id : revokedUsers) {
            keys[i++] = getPersonalKey(id).get();
        }
        return null;
    }

    /**
     * @param i An index
     * @return The identity with the given index
     */
    public NaorPinkasIdentity getIdentity(int i) {
        // first t users are dummies
        return getUserKey(i).getIdentity();
    }

    private NaorPinkasPersonalKey getUserKey(int i) {
        // first t users are dummies
        return keyGen.get(t + i);
    }
    private NaorPinkasPersonalKey getDummyKey(int i) {
        return keyGen.get(i);
    }

    /**
     * Revokes a user.
     * @param id The identity of the user
     */
    public void revoke(NaorPinkasIdentity id) throws NoMoreRevocationsPossibleError {
        if (revokedUsers.size() == t) {
            throw new NoMoreRevocationsPossibleError();
        }
        revokedUsers.add(id);
    }
    
    /**
     * @param id The identity of the user
     * @return Whether the user is revoked.
     */
    public boolean isRevoked(NaorPinkasIdentity id) {
        return revokedUsers.contains(id);
    }
    
    /**
     * @param id The identity to look up
     * @return The private key of the user or absent if no
     * such user exists
     */
    public Optional<NaorPinkasPersonalKey> getPersonalKey(NaorPinkasIdentity id) {
        return Optional.fromNullable(userByIdentity.get(id));
    }
}
