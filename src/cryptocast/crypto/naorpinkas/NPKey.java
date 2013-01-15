package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.security.PrivateKey;

import cryptocast.crypto.CyclicGroupOfPrimeOrder;

/**
 * A user's personal key in the Naor-Pinkas broadcast encryption scheme.
 * It consists of a value $I$ and the value $P(I)$, which is the value of 
 * the Naor-Pinkas polynomial at $I$.
 */
public class NPKey<T, G extends CyclicGroupOfPrimeOrder<T>>
                        implements PrivateKey {
    private static final long serialVersionUID = 654772327240529095L;

    private BigInteger i, pi;
    private G group;
    private NPIdentity id;
    
    /**
     * Creates a new instance of NaorPinkasPersonalKey with the given parameters.
     * 
     * @param i A point $I$.
     * @param pi $P(I)$, the value of the polynomial at $I$.
     * @param schnorr The schnorr group.
     */
    protected NPKey(BigInteger i, BigInteger pi, 
                                    G group) {
        this.i = i;
        id = new NPIdentity(i);
        this.pi = pi;
        this.group = group;
    }

    /**
     * @return The NP group.
     */
    public G getGroup() {
        return group;
    }
    
    /**
     * @param r The integer $r$.
     * @param gr The value $g^r$.
     * @return a NP share constructed from this key and the given values.
     */
    public NPShare<T, G> getShare(BigInteger r, T gr) {
        T x = group.pow(gr, pi);
        return new NPShare<T, G>(i, x, group);
    }
    
    /**
     * @param r The integer $r$
     * @return a NP share constructed from this key and the given $r$.
     */
    public NPShare<T, G> getShare(BigInteger r) {
        return getShare(r, group.getPowerOfG(r));
    }

    /**
     * @return the NP identity.
     */
    public NPIdentity getIdentity() {
        return id;
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
