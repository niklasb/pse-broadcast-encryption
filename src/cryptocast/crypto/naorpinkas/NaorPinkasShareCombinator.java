package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

/**
 * Allows to restore a number from a sufficient number of Naor-Pinkas shares.
 */
public class NaorPinkasShareCombinator implements 
             ShareCombinator<BigInteger, 
                             NaorPinkasShare, 
                             LagrangeInterpolation<BigInteger>> {
    /**
     * Restores a secret from several Naor-Pinkas shares.
     * @param shares The shares
     * @param additionalInfo the precomputed Lagrange coefficients
     * @return The reconstructed secret or absent if the information represented
     * by the given shares is insufficient to restore it.
     */
    @Override
    public Optional<BigInteger> restore(ImmutableList<NaorPinkasShare> shares,
                                        LagrangeInterpolation<BigInteger> lagrange) {
        if (hasMissingShares(shares) || hasRedundantShares(shares)) {
            return Optional.absent();
        }

        Field<BigInteger> modP = shares.get(0).getGroup().getFieldModP();
        
        lagrange.setXs(ImmutableSet.copyOf(NaorPinkasShare.getXsFromShares(shares)));
        Map<BigInteger, BigInteger> coeffs = lagrange.getCoefficients();
        BigInteger res = modP.one();
        for (NaorPinkasShare share : shares) {
            BigInteger c = coeffs.get(share.getI());
            res = modP.multiply(res, modP.pow(share.getGRPI(), c));
        }
        return Optional.of(res);
    }
    
    public Optional<BigInteger> restore(ImmutableList<NaorPinkasShare> shares, 
                                        SchnorrGroup schnorr) {
        if (hasMissingShares(shares) || hasRedundantShares(shares)) {
            return Optional.absent();
        }
        return restore(shares, new LagrangeInterpolation<BigInteger>(schnorr.getFieldModQ()));
    }
    
    public boolean hasMissingShares(ImmutableList<NaorPinkasShare> shares) {
        int t = shares.get(0).getT();
        if (shares.size() < t + 1) {
            return true;
        }
        return false;
    }
    
    public boolean hasRedundantShares(ImmutableList<NaorPinkasShare> shares) {
        ImmutableList<NaorPinkasShare> sortedShares = 
                Ordering.natural().immutableSortedCopy(shares);
        BigInteger lastX = null;
        for (NaorPinkasShare share : sortedShares) {
            BigInteger x = share.getI();
            if (x.equals(lastX)) {
                return true;
            }
            lastX = x;
        }
        return false;
    }
}