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

    protected int t;
    protected BigInteger i, pi;
    protected SchnorrGroup group;

    protected NaorPinkasPersonalKey(int t, BigInteger i, BigInteger pi, 
                                    SchnorrGroup group) {
        this.t = t;
        this.i = i;
        this.pi = pi;
        this.group = group;
    }

    public NaorPinkasShare getShare(BigInteger r) {
        BigInteger x = group.pow(group.getPowerOfG(pi), r);
        return new NaorPinkasShare(t, r, i, x, group);
    }

    public NaorPinkasIdentity getIdentity() {
        return new NaorPinkasIdentity(i);
    }

    public String getAlgorithm() { return "Naor-Pinkas"; }
    public byte[] getEncoded() { return null; }
    public String getFormat() { return null; }
}
