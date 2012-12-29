package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import cryptocast.util.Generator;
import cryptocast.util.OptimisticGenerator;
import cryptocast.util.ByteUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

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
    private SchnorrGroup schnorr;
    private Map<NaorPinkasIdentity, NaorPinkasPersonalKey> keyByIdentity =
                 new HashMap<NaorPinkasIdentity, NaorPinkasPersonalKey>();
    private Set<NaorPinkasIdentity> revokedUsers =
                 new HashSet<NaorPinkasIdentity>();
    private Generator<NaorPinkasPersonalKey> keyGen;
    private BigInteger gp0;  // $g^P(0)$

    private static SecureRandom rnd = new SecureRandom();

    private NaorPinkasServer(int t, SchnorrGroup schnorr,
                             Generator<NaorPinkasPersonalKey> keyGen,
                             Polynomial<BigInteger> poly) {
        this.t = t;
        this.schnorr = schnorr;
        this.keyGen = keyGen;
        this.gp0 = schnorr.getPowerOfG(poly.evaluate(BigInteger.ZERO));
    }

    public int getT() {
        return t;
    }
    
    public static NaorPinkasServer generate(int t, SchnorrGroup schnorr) {
        Polynomial<BigInteger> poly = 
                Polynomial.createRandomPolynomial(rnd, schnorr.getFieldModQ(), t);
        Generator<NaorPinkasPersonalKey> keyGen = 
                new OptimisticGenerator<NaorPinkasPersonalKey>(
                        new NaorPinkasKeyGenerator(
                                t, new SecureRandom(), schnorr, poly));
        return new NaorPinkasServer(t, schnorr, keyGen, poly);
    }

    /**
     * Encrypts a secret.
     * @param secret the secret
     * @return The cipher text
     */
    public byte[] encrypt(byte[] secret) {
        byte[] bytes = new byte[secret.length + 1];
        bytes[0] = 0x01;
        System.arraycopy(secret, 0, bytes, 1, secret.length);
        return ByteUtils.pack(encryptNumber(new BigInteger(bytes)));
    }

    public NaorPinkasMessage encryptNumber(BigInteger secret) {
        // interpret bytes as the one's complement 
        // representation of a number
        BigInteger r = schnorr.getFieldModP().randomElement(rnd),
                   grp0 = schnorr.getFieldModP().pow(gp0, r), // g^{r P(0)}
                   xor = grp0.xor(secret);
        checkArgument(xor.compareTo(schnorr.getP()) < 0, "Secret is too large to encrypt");
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        int i = 0;
        int dummies = t - revokedUsers.size();
        while (i < dummies) {
            shares.add(getDummyKey(i).getShare(r));
            ++i;
        }
        for (NaorPinkasIdentity id : revokedUsers) {
            shares.add(getPersonalKey(id).get().getShare(r));
        }
        return new NaorPinkasMessage(t, r, xor, schnorr, shares.build());
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
        NaorPinkasPersonalKey key = keyGen.get(t + i);
        keyByIdentity.put(key.getIdentity(), key);
        return key;
    }

    private NaorPinkasPersonalKey getDummyKey(int i) {
        return keyGen.get(i);
    }

    public boolean revoke(NaorPinkasIdentity id) throws NoMoreRevocationsPossibleError {
        if (revokedUsers.size() == t) {
            throw new NoMoreRevocationsPossibleError();
        }
        return revokedUsers.add(id);
    }
    
    public boolean unrevoke(NaorPinkasIdentity id) {
        return revokedUsers.remove(id);
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
        return Optional.fromNullable(keyByIdentity.get(id));
    }
}