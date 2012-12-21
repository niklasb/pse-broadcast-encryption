package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;

/**
 * Allows to restore a number from a sufficient number of Naor-Pinkas shares.
 */
public class NaorPinkasShareCombinator implements ShareCombinator<BigInteger, NaorPinkasShare> {
    /**
     * Restores a secret from several Naor-Pinkas shares.
     * @param shares The shares
     * @return The reconstructed secret or absent if the information represented
     * by the given shares is insufficient to restore it.
     */
    @Override
    public Optional<BigInteger> restore(List<NaorPinkasShare> shares) {
        int t = shares.get(0).t;
        ModularExponentiationGroup group = shares.get(0).group;
        if (shares.size() != t + 1) {
            // missing information
            return Optional.absent();
        }
        List<NaorPinkasShare> sharesCopy = new ArrayList<NaorPinkasShare>(shares);
        Collections.sort(sharesCopy);
        BigInteger[] xs = new BigInteger[sharesCopy.size()];
        int i = 0;
        for (NaorPinkasShare share : sharesCopy) {
            xs[i] = share.getI();
            if (i > 0 && xs[i] == xs[i-1]) {
                // redundant information
                return Optional.absent();
            }
            i++;
        }
        BigInteger[] lambdas = LagrangeInterpolation.computeCoefficients(group, xs);
        BigInteger res = group.one();
        i = 0;
        for (NaorPinkasShare share : sharesCopy) {
            res = group.multiply(res, group.pow(share.getGRPI(), lambdas[i]));
            i++;
        }
        return Optional.of(res);
    }
}