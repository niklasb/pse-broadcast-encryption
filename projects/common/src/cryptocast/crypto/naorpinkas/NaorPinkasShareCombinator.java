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
        SchnorrGroup schnorr = shares.get(0).group;
        if (shares.size() < t + 1) {
            // missing information
            System.out.println("test1");
            return Optional.absent();
        }
        List<NaorPinkasShare> sharesCopy = new ArrayList<NaorPinkasShare>(shares);
        Collections.sort(sharesCopy);
        BigInteger[] xs = new BigInteger[t + 1];
        int i = 0;
        for (NaorPinkasShare share : sharesCopy) {
            if (i >= t + 1) { break; }
            xs[i] = share.getI();
            if (i > 0 && xs[i].equals(xs[i-1])) {
                // redundant information
                return Optional.absent();
            }
            i++;
        }
        Field<BigInteger> modQ = schnorr.getFieldModQ(),
                          modP = schnorr.getFieldModP();
        BigInteger[] lambdas = LagrangeInterpolation.computeCoefficients(modQ, xs);
        BigInteger res = modP.one();
        i = 0;
        for (NaorPinkasShare share : sharesCopy) {
            res = modP.multiply(res, modP.pow(share.getGRPI(), lambdas[i]));
            i++;
        }
        return Optional.of(res);
    }
}