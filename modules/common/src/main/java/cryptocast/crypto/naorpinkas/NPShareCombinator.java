package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;

import java.math.BigInteger;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

/**
 * Allows to restore a number from a sufficient number of Naor-Pinkas shares.
 */
public class NPShareCombinator<T, G extends CyclicGroupOfPrimeOrder<T>> {
    private int t;
    private G group;

    public NPShareCombinator(int t, G group) {
        this.t = t;
        this.group = group;
    }

    /**
     * Restores a secret from several Naor-Pinkas shares.
     * @param shares The shares
     * @param lagrange The precomputed Lagrange coefficients
     *
     * @return The reconstructed secret or absent if the information represented
     * by the given shares is insufficient to restore it.
     */
    public Optional<T> restore(List<NPShare<T, G>> shares,
                               LagrangeInterpolation<BigInteger> lagrange) {
        if (hasMissingShares(shares) || hasRedundantShares(shares)) {
            return Optional.absent();
        }

        ImmutableList.Builder<T> bases = ImmutableList.builder();
        ImmutableList.Builder<BigInteger> exponents = ImmutableList.builder();

        lagrange.setXs(ImmutableSet.copyOf(NPShare.getXsFromShares(shares)));
        for (NPShare<T, G> share : shares) {
            bases.add(share.getGRPI());
            BigInteger e = lagrange.getCoefficients().get(share.getI());
            assert e != null;
            exponents.add(e);
        }
        return Optional.of(group.multiexp(bases.build(), exponents.build()));
    }

    /**
     * Checks whether missing shares exist.
     *
     * @param shares The shares to check.
     * @return <code>true</code> if there are any missing shares, <code>false</code> otherwise.
     */
    public boolean hasMissingShares(List<NPShare<T, G>> shares) {
        if (shares.size() < t + 1) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether redundant shares exist.
     *
     * @param shares The shares.
     * @return <code>true</code> if there are any redundant shares, <code>false</code> otherwise.
     */
    public boolean hasRedundantShares(List<NPShare<T, G>> shares) {
        ImmutableList<NPShare<T, G>> sortedShares =
                Ordering.natural().immutableSortedCopy(shares);
        BigInteger lastX = null;
        for (NPShare<T, G> share : sortedShares) {
            BigInteger x = share.getI();
            if (x.equals(lastX)) {
                return true;
            }
            lastX = x;
        }
        return false;
    }
}
