package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.math.BigInteger;
import java.util.Collection;

import com.google.common.base.Optional;

/**
 * Allows to restore a number from a sufficient number of Naor-Pinkas shares
 */
public class NaorPinkasShareCombinator implements ShareCombinator<BigInteger, NaorPinkasShare> {
    /**
     * Restores a secret from several Naor-Pinkas shares.
     * @param shares The shares
     * @return The reconstructed secret or absent if the information represented
     * by the given shares is insufficient to restore it.
     */
    public Optional<BigInteger> restore(Collection<NaorPinkasShare> shares) {
        return null;
    }
}
