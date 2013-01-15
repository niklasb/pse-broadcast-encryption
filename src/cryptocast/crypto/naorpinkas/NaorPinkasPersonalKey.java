package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.security.PrivateKey;

import cryptocast.crypto.CyclicGroupOfPrimeOrder;

/**
 * A user's personal key in the Naor-Pinkas broadcast encryption scheme.
 * It consists of a value $I$ and the value $P(I)$, which is the value of 
 * the Naor-Pinkas polynomial at $I$.
 */
public class NaorPinkasPersonalKey<T>
                        implements PrivateKey {
    private static final long serialVersionUID = 654772327240529095L;

    private int t;
    private BigInteger i, pi;
    private CyclicGroupOfPrimeOrder<T> group;

    /**
     * Creates a new instance of NaorPinkasPersonalKey with the given parameters.
     * 
     * @param t The $t$, degree of the polynomial.
     * @param i The $I$ of the polynomial.
     * @param pi The $P(I)$ of the polynomial, the value of the polynomial at $I$.
     * @param schnorr The schnorr group.
     */
    protected NaorPinkasPersonalKey(int t, BigInteger i, BigInteger pi, 
                                    CyclicGroupOfPrimeOrder<T> group) {
        this.t = t;
        this.i = i;
        this.pi = pi;
        this.group = group;
    }

    /**
     * @return The NP group.
     */
    public CyclicGroupOfPrimeOrder<T> getGroup() {
        return group;
    }
    
    /**
     * @param r The integer $r$.
     * @param gr The value $g^r$.
     * @return a NP share constructed from this key and the given values.
     */
    public NaorPinkasShare<T> getShare(BigInteger r, T gr) {
        T x = group.pow(gr, pi);
        return new NaorPinkasShare<T>(t, r, i, x, group);
    }

    /**
     * @return the NP identity.
     */
    public NaorPinkasIdentity getIdentity() {
        return new NaorPinkasIdentity(i);
    }

    /**
     * @return the name of the algorithm associated with this key.
     */
    public String getAlgorithm() { return "Naor-Pinkas"; }
    
    /**
     * @return null
     */
    public byte[] getEncoded() { return null; }
    
    /**
     * @return null
     */
    public String getFormat() { return null; }
}
