package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import cryptocast.crypto.Protos.BInteger;
import cryptocast.crypto.naorpinkas.Protos.*;
import cryptocast.util.MapUtils;

import java.math.BigInteger;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * A client in the Naor-Pinkas broadcast encryption scheme.
 */
public abstract class NaorPinkasClient<T> implements Decryptor<byte[]> {
    private NaorPinkasPersonalKey<T> key;
    private NaorPinkasShareCombinator<T> combinator = 
                   new NaorPinkasShareCombinator<T>();
    private CyclicGroupOfPrimeOrder<T> group;
    
    /**
     * Initializes a Naor-Pinkas broadcast client.
     * @param key The personal key used to reconstruct a secret from the stream.
     */
    protected NaorPinkasClient(NaorPinkasPersonalKey<T> key) {
        this.key = key;
        group = key.getGroup();
    }
    
    public CyclicGroupOfPrimeOrder<T> getGroup() { return group; }
    
    protected T decryptItem(NaorPinkasMessageCommon common, List<NaorPinkasShare<T>> shares) 
                  throws InsufficientInformationError {
        BigInteger r = unpackBigInt(common.getR());
        ImmutableList.Builder<BigInteger> coeffs = ImmutableList.builder();
        for (BInteger c : common.getCoefficientsList()) {
            coeffs.add(unpackBigInt(c));
        }
        LagrangeInterpolation<BigInteger> lagrange = 
                     new LagrangeInterpolation<BigInteger>(group.getFieldModOrder(), 
                             MapUtils.zip(NaorPinkasShare.getXsFromShares(shares), 
                                          coeffs.build()));
        ImmutableList<NaorPinkasShare<T>> allShares = ImmutableList.<NaorPinkasShare<T>>builder()
                .addAll(shares)
                .add(key.getShare(r, group.getPowerOfG(r)))
                .build();
        Optional<T> mInterpol = combinator.restore(allShares, lagrange);
        if (!mInterpol.isPresent()) {
            throw new InsufficientInformationError(
                    "Cannot restore secret: Redundant or missing information");
        }
        return mInterpol.get();
    }
    
    protected BigInteger unpackBigInt(BInteger b) {
        return new BigInteger(b.getTwoComplement().toByteArray());
    }
}