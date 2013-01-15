package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.util.List;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.CyclicGroupOfPrimeOrder;

/**
 * A share in the Naor-Pinkas broadcast encryption scheme. It consists of a tuple
 * $(r, I, g^{r P(I)})$. $t + 1$ distinct shares of this form are sufficient to restore the
 * value $g^{r P(0)}$, where $t$ is the degree of the polynomial $P$.
 */
public class NaorPinkasShare<T> implements Comparable<NaorPinkasShare<T>> {
    private int t;
    private BigInteger r, i;
    private T grpi;
    private CyclicGroupOfPrimeOrder<T> group;

    /**
     * Creates a new instance of NaorPinkasShare with the given parameters,
     * ($(r, I, g^{r \cdot P(I)})$)
     * 
     * @param t The degree of the polynomial $P$
     * @param r The integer $r < q$.
     * @param i The integer $i < q$.
     * @param grpi The value $g^{r \cdot P(I)}) \in G$.
     * @param group The NP group.
     */
    public NaorPinkasShare(int t, BigInteger r, BigInteger i, T grpi,
                           CyclicGroupOfPrimeOrder<T> group) {
        this.t = t;
        this.r = r;
        this.i = i;
        this.grpi = grpi;
        this.group = group;
    }
    
    /**
     * @return The degree of the polynomial $P$
     */
    public int getT() { return t; }
    
    /**
     * @return The cyclic group used by NP.
     */
    public CyclicGroupOfPrimeOrder<T> getGroup() { return group; }
    
    /**
     * @return The integer $r$.
     */
    public BigInteger getR() { return r; }
    
    /**
     * @return The integer $I$.
     */
    public BigInteger getI() { return i; }
    
    /**
     * @return $g^{r \cdot P(I)}$
     */
    public T getGRPI() { return grpi; }

    /**
     * @return The identity
     */
    public NaorPinkasIdentity getIdentity() {
        return new NaorPinkasIdentity(i);
    }

    @Override
    public int compareTo(NaorPinkasShare<T> other) {
        return i.compareTo(other.i);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        NaorPinkasShare<T> other;
        try {
            other = (NaorPinkasShare<T>)other_;
        } catch (Throwable e) { return false; }
        return t == other.t 
            && r.equals(other.r) 
            && i.equals(other.i)
            && grpi.equals(other.grpi)
            && group.equals(other.group);
    }
    
    /**
     * Returns a list of points from shares.
     * 
     * @param shares The list of shares.
     * @return list of points.
     */
    public static <T, G> ImmutableList<BigInteger> getXsFromShares(
                                  List<NaorPinkasShare<T>> shares) {
        ImmutableList.Builder<BigInteger> xs = ImmutableList.builder();
        for (NaorPinkasShare<T> share : shares) {
            xs.add(share.getI());
        }
        return xs.build();
    }
}
