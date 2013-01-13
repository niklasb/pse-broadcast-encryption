package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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
    public Optional<BigInteger> restore(List<NaorPinkasShare> shares,
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
    
    /**
     * Restores a secret from several Naor-Pinkas shares and a shnorr group.
     * 
     * @param shares The shares.
     * @param schnorr The scnorr group.
     * @return The reconstructed secret or absent if the information represented
     * by the given shares is insufficient to restore it.
     */
    public Optional<BigInteger> restore(List<NaorPinkasShare> shares, 
                                        SchnorrGroup schnorr) {
        if (hasMissingShares(shares) || hasRedundantShares(shares)) {
            return Optional.absent();
        }
        return restore(shares, new LagrangeInterpolation<BigInteger>(schnorr.getFieldModQ()));
    }
    
    /**
     * Checks whether missing shares exist.
     * 
     * @param shares The shares to check.
     * @return <code>true</code> if there are any missing shares, <code>false</code> otherwise.
     */
    public boolean hasMissingShares(List<NaorPinkasShare> shares) {
        int t = shares.get(0).getT();
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
    public boolean hasRedundantShares(List<NaorPinkasShare> shares) {
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