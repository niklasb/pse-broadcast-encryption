package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.security.PrivateKey;

import cryptocast.crypto.SchnorrGroup;

/**
 * A user's personal key in the Naor-Pinkas broadcast encryption scheme.
 * It consists of a value $I$ and the value $P(I)$, which is the value of 
 * the Naor-Pinkas polynomial at $I$.
 */
public class NaorPinkasPersonalKey implements PrivateKey {
    private static final long serialVersionUID = 654772327240529095L;

    private int t;
    private BigInteger i, pi;
    private SchnorrGroup schnorr;

    /**
     * Creates a new instance of NaorPinkasPersonalKey with the given parameters.
     * 
     * @param t The $t$, degree of the polynomial.
     * @param i The $I$ of the polynomial.
     * @param pi The $P(I)$ of the polynomial, the value of the polynomial at $I$.
     * @param schnorr The schnorr group.
     */
    protected NaorPinkasPersonalKey(int t, BigInteger i, BigInteger pi, 
                                    SchnorrGroup schnorr) {
        this.t = t;
        this.i = i;
        this.pi = pi;
        this.schnorr = schnorr;
    }

    /**
     * Returns the schnorr group.
     * 
     * @return The schnorr group.
     */
    public SchnorrGroup getSchnorr() {
        return schnorr;
    }
    
    /**
     * Returns a naor-pinkas share for the given values.
     * 
     * @param r The $r$ value.
     * @param gr The $gr$ value.
     * @return a naor-pinkas share.
     */
    public NaorPinkasShare getShare(BigInteger r, BigInteger gr) {
        BigInteger x = schnorr.getFieldModP().pow(gr, pi);
        return new NaorPinkasShare(t, r, i, x, schnorr);
    }

    /**
     * Returns the naor-pinkas identity.
     * 
     * @return the naor-pinkas identity.
     */
    public NaorPinkasIdentity getIdentity() {
        return new NaorPinkasIdentity(i);
    }

    /**
     * Returns the name of the algorithm associated with this key.
     */
    public String getAlgorithm() { return "Naor-Pinkas"; }
    /**
     * Returns null
     */
    public byte[] getEncoded() { return null; }
    /**
     * Returns null
     */
    public String getFormat() { return null; }
}
