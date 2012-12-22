package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import cryptocast.crypto.SchnorrGroup;
import cryptocast.util.Packable;
import static cryptocast.util.ByteUtils.*;

/**
 * A share in the Naor-Pinkas broadcast encryption scheme. It consists of a tuple
 * $(r, I, g^{r P(I)})$. $t + 1$ distinct shares of this form are sufficient to restore the
 * value $g^{r P(0)}$, where $t$ is the degree of the polynomial $P$.
 */
public class NaorPinkasShare implements Comparable<NaorPinkasShare>, Packable {
    protected int t;
    protected BigInteger r, i, grpi;
    protected SchnorrGroup schnorr;

    public NaorPinkasShare(int t, BigInteger r, BigInteger i, BigInteger grpi,
                           SchnorrGroup schnorr) {
        this.t = t;
        this.r = r;
        this.i = i;
        this.grpi = grpi;
        this.schnorr = schnorr;
    }
    
    public int getT() { return t; }
    public SchnorrGroup getGroup() { return schnorr; }
    public BigInteger getR() { return r; }
    public BigInteger getI() { return i; }
    public BigInteger getGRPI() { return grpi; }

    @Override
    public int compareTo(NaorPinkasShare other) {
        return i.compareTo(other.i);
    }

    @Override
    public int getMaxSpace() {
        return 2 * schnorr.getMaxNumberSpace();
    }

    @Override
    public void pack(ByteBuffer buf) {
        putBigInt(buf, i);
        putBigInt(buf, grpi);
    }

    public static NaorPinkasShare unpack(int t, 
                                         BigInteger r, 
                                         SchnorrGroup group,
                                         ByteBuffer buf) {
        BigInteger i = getBigInt(buf),
                   grpi = getBigInt(buf);
        return new NaorPinkasShare(t, r, i, grpi, group);
    }
    
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        NaorPinkasShare other = (NaorPinkasShare)other_;
        return t == other.t 
            && r.equals(other.r) 
            && i.equals(other.i)
            && grpi.equals(other.grpi)
            && schnorr.equals(other.schnorr);
    }
}
