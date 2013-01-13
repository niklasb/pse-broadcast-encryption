package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

import com.google.common.collect.ImmutableList;

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

    /**
     * Creates a new instance of NaorPinkasShare with the given parameters,
     * ($(r, I, g^{r P(I)})$)
     * 
     * @param t The degree of the polynomial $P$
     * @param r The $r$ value.
     * @param i The $i$ value.
     * @param grpi The $g^{r P(I)})$ value.
     * @param schnorr The schnorr group.
     */
    public NaorPinkasShare(int t, BigInteger r, BigInteger i, BigInteger grpi,
                           SchnorrGroup schnorr) {
        this.t = t;
        this.r = r;
        this.i = i;
        this.grpi = grpi;
        this.schnorr = schnorr;
    }
    /**
     * Returns the degree of the polynomial $P$
     * 
     * @return The degree of the polynomial $P$
     */
    public int getT() { return t; }
    /**
     * Returns the schnorr group.
     * 
     * @return The schnorr group.
     */
    public SchnorrGroup getGroup() { return schnorr; }
    /**
     * Returns the $r$ value.
     * 
     * @return The $r$ value.
     */
    public BigInteger getR() { return r; }
    /**
     * Returns the $i$ value.
     * 
     * @return The $i$ value.
     */
    public BigInteger getI() { return i; }
    /**
     * Returns the $grpi$ value.
     * 
     * @return the $grpi$ value.
     */
    public BigInteger getGRPI() { return grpi; }

    /**
     * Returns the identity.
     * 
     * @return The identity
     */
    public NaorPinkasIdentity getIdentity() {
        return new NaorPinkasIdentity(i);
    }

    @Override
    public int compareTo(NaorPinkasShare other) {
        return i.compareTo(other.i);
    }

    @Override
    public int getMaxSpace() {
        return 2 * schnorr.getFieldModP().getMaxNumberSpace();
    }

    @Override
    public void pack(ByteBuffer buf) {
        putBigInt(buf, i);
        putBigInt(buf, grpi);
    }

    /**
     * Unpacks the given values into a naor-pinkas share.
     * 
     * @param t The $t$ value.
     * @param r The $r$ value.
     * @param group The schnorr group.
     * @param buf A byte buffer.
     * @return Naor-pinkas share.
     */
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
    
    /**
     * Returns a list of points from shares.
     * 
     * @param shares The list of shares.
     * @return list of points.
     */
    public static ImmutableList<BigInteger> getXsFromShares(
                                  List<NaorPinkasShare> shares) {
        ImmutableList.Builder<BigInteger> xs = ImmutableList.builder();
        for (NaorPinkasShare share : shares) {
            xs.add(share.getI());
        }
        return xs.build();
    }
}
