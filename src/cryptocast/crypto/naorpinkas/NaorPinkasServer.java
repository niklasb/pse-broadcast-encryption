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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory
            .getLogger(NaorPinkasServer.class);
    
    private int t;
    private SchnorrGroup schnorr;
    private Map<NaorPinkasIdentity, NaorPinkasPersonalKey> keyByIdentity =
                 new HashMap<NaorPinkasIdentity, NaorPinkasPersonalKey>();
    private Set<NaorPinkasIdentity> revokedUsers =
                 new HashSet<NaorPinkasIdentity>();
    private Generator<NaorPinkasPersonalKey> keyGen;
    private BigInteger gp0;  // $g^P(0)$
    private LagrangeInterpolation<BigInteger> lagrange;

    private static SecureRandom rnd = new SecureRandom();

    private NaorPinkasServer(int t, SchnorrGroup schnorr,
                             Generator<NaorPinkasPersonalKey> keyGen,
                             Polynomial<BigInteger> poly,
                             LagrangeInterpolation<BigInteger> lagrange) {
        this.t = t;
        this.schnorr = schnorr;
        this.keyGen = keyGen;
        this.gp0 = schnorr.getPowerOfG(poly.evaluate(BigInteger.ZERO));
        this.lagrange = lagrange;
    }

    /**
     * Returns the degree of the polynomial.
     * 
     * @return The degree of the polynomial.
     */
    public int getT() {
        return t;
    }
    
    /**
     * Generates a naor-pinkas server instance.
     * 
     * @param t The degree of the polynomial.
     * @param schnorr The schnorr group.
     * @return Naor-pinkas server instance.
     */
    public static NaorPinkasServer generate(int t, SchnorrGroup schnorr) {
        Field<BigInteger> modQ = schnorr.getFieldModQ();
        log.debug("Generating random polynomial");
        long start = System.currentTimeMillis();
        Polynomial<BigInteger> poly = 
                Polynomial.createRandomPolynomial(rnd, modQ, t + 1);
        log.debug("Took {} ms", System.currentTimeMillis() - start);
        log.debug("Setting up dummy keys");
        start = System.currentTimeMillis();
        Generator<NaorPinkasPersonalKey> keyGen = 
                new OptimisticGenerator<NaorPinkasPersonalKey>(
                        new NaorPinkasKeyGenerator(
                                t, rnd, schnorr, poly));
        ImmutableList.Builder<BigInteger> dummyXs = ImmutableList.builder();
        for (int i = 0; i < t; ++i) {
            dummyXs.add(keyGen.get(i).getIdentity().getI());
        }
        log.debug("Took {} ms", System.currentTimeMillis() - start);
        log.debug("Computing initial lagrange coefficients");
        start = System.currentTimeMillis();
        LagrangeInterpolation<BigInteger> lagrange = 
                LagrangeInterpolation.fromXs(modQ, dummyXs.build());
        log.debug("Took {} ms", System.currentTimeMillis() - start);
        return new NaorPinkasServer(t, schnorr, keyGen, poly, lagrange);
    }

    /**
     * Encrypts a secret.
     * @param secret the secret.
     * @return The cipher text.
     */
    public byte[] encrypt(byte[] secret) {
        byte[] bytes = new byte[secret.length + 1];
        // explicitly set the sign bit so we can safely remove it on the other side
        bytes[0] = 0x01;
        System.arraycopy(secret, 0, bytes, 1, secret.length);
        return ByteUtils.pack(encryptNumber(new BigInteger(bytes)));
    }

    /**
     * Encrypts a message given the secret code.
     * 
     * @param secret The secret code.
     * @return Naor-pinkas message.
     */
    public NaorPinkasMessage encryptNumber(BigInteger secret) {
        BigInteger r = schnorr.getFieldModP().randomElement(rnd),
                   grp0 = schnorr.getFieldModP().pow(gp0, r), // g^{r P(0)}
                   xor = grp0.xor(secret),
                   gr = schnorr.getPowerOfG(r);
        checkArgument(xor.compareTo(schnorr.getP()) < 0, "Secret is too large to encrypt");
        
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        int i = 0;
        int dummies = t - revokedUsers.size();
        while (i < dummies) {
            NaorPinkasShare share = getDummyKey(i).getShare(r, gr);
            shares.add(share);
            ++i;
        }
        for (NaorPinkasIdentity id : revokedUsers) {
            NaorPinkasShare share = getPersonalKey(id).get().getShare(r, gr);
            shares.add(share);
        }
        
        return new NaorPinkasMessage(t, r, xor, schnorr, lagrange, shares.build());
    }

    /**
     * Returns the identity with the given index.
     * 
     * @param i An index.
     * @return The identity with the given index.
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

    /**
     * Revokes a user.
     * 
     * @param id The identity of the user
     * @return true, if the set of revoked users changed or false otherwise
     */
    public boolean revoke(NaorPinkasIdentity id) throws NoMoreRevocationsPossibleError {
        if (revokedUsers.size() == t) {
            throw new NoMoreRevocationsPossibleError();
        }
        // TODO abstract this away
        // remove highest dummy key and add new identity
        lagrange.removeX(getDummyKey(t - revokedUsers.size() - 1).getIdentity().getI());
        lagrange.addX(id.getI());
        return revokedUsers.add(id);
    }
    
    /**
     * Authorizes a user.
     * 
     * @param id The identity of the user
     * @return true, if the set of revoked users changed or false otherwise
     */
    public boolean unrevoke(NaorPinkasIdentity id) {
        // TODO abstract this away
        // remove old identity and add new dummy
        lagrange.removeX(id.getI());
        lagrange.addX(getDummyKey(t - revokedUsers.size()).getIdentity().getI());
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