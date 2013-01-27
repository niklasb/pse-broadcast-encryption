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
public class NPShare<T, G extends CyclicGroupOfPrimeOrder<T>>
                                  implements Comparable<NPShare<T, G>> {
    private BigInteger i;
    private T grpi;
    private G group;

    /**
     * Creates a new NP share from a tuple $(I, g^{r \cdot P(I)})$
     *
     * @param i An integer $i < q$.
     * @param grpi The value $g^{r \cdot P(I)}) \in G$.
     * @param group The NP group.
     */
    public NPShare(BigInteger i, T grpi, G group) {
        this.i = i;
        this.grpi = grpi;
        this.group = group;
    }

    /**
     * @return The cyclic group used by NP.
     */
    public G getGroup() { return group; }

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
    public NPIdentity getIdentity() {
        return new NPIdentity(i);
    }

    @Override
    public int compareTo(NPShare<T, G> other) {
        return i.compareTo(other.i);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        NPShare<T, G> other;
        try {
            other = (NPShare<T, G>)other_;
        } catch (Throwable e) { return false; }
        return i.equals(other.i)
            && grpi.equals(other.grpi)
            && group.equals(other.group);
    }

    /**
     * Returns a list of points from shares.
     *
     * @param shares The list of shares.
     * @return The points $I_i$ extracted from the given shares
     */
    public static <T, G extends CyclicGroupOfPrimeOrder<T>>
                ImmutableList<BigInteger> getXsFromShares(
                                  List<NPShare<T, G>> shares) {
        ImmutableList.Builder<BigInteger> xs = ImmutableList.builder();
        for (NPShare<T, G> share : shares) {
            xs.add(share.getI());
        }
        return xs.build();
    }
}
