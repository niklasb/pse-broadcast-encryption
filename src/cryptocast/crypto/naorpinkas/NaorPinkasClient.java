package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import cryptocast.util.MapUtils;

import java.math.BigInteger;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

/**
 * A client in the Naor-Pinkas broadcast encryption scheme.
 */
public abstract class NaorPinkasClient<T, G extends CyclicGroupOfPrimeOrder<T>> 
                                 implements Decryptor<byte[]> {
    private NaorPinkasPersonalKey<T, G> key;
    private G group;
    
    /**
     * Initializes a Naor-Pinkas broadcast client.
     * @param key The personal key used to reconstruct a secret from the stream.
     */
    protected NaorPinkasClient(NaorPinkasPersonalKey<T, G> key) {
        this.key = key;
        group = key.getGroup();
    }

    public G getGroup() { return group; }

    protected abstract NaorPinkasShare<T, G> readShare(ByteArrayDataInput in);

    public NaorPinkasMessage<T, G> unpackMessage(byte[] msg) {
        ByteArrayDataInput in = ByteStreams.newDataInput(msg);
        int t = in.readInt();
        BigInteger r = new BigInteger(readBytes(in));
        byte[] encryptedSecret = readBytes(in);
        ImmutableList.Builder<BigInteger> lagrangeCoeffs = ImmutableList.builder();
        for (int i = 0; i < t; ++i) {
            lagrangeCoeffs.add(new BigInteger(readBytes(in)));
        }
        ImmutableList.Builder<NaorPinkasShare<T, G>> shares = ImmutableList.builder();
        for (int i = 0; i < t; ++i) {
            shares.add(readShare(in));
        }
        return new NaorPinkasMessage<T, G>(t, r, encryptedSecret, group, 
                                           lagrangeCoeffs.build(), shares.build());
    }

    protected byte[] readBytes(ByteArrayDataInput in) {
        int len = in.readInt();
        byte[] data = new byte[len];
        in.readFully(data);
        return data;
    }
    
    protected abstract byte[] decryptSecretWithItem(byte[] encryptedSecret, T item);
    
    @Override
    public byte[] decrypt(byte[] cipher) throws InsufficientInformationError {
        NaorPinkasMessage<T, G> msg = unpackMessage(cipher);
        NaorPinkasShareCombinator<T, G> combinator = 
                new NaorPinkasShareCombinator<T, G>(msg.getT(), group);
        List<NaorPinkasShare<T, G>> allShares = ImmutableList.<NaorPinkasShare<T, G>>builder()
                                                  .addAll(msg.getShares())
                                                  .add(key.getShare(msg.getR()))
                                                  .build();
        LagrangeInterpolation<BigInteger> lagrange = 
                new LagrangeInterpolation<BigInteger>(group.getFieldModOrder(), 
                        MapUtils.zip(NaorPinkasShare.getXsFromShares(msg.getShares()), 
                                msg.getLagrangeCoeffs()));
        Optional<T> mInterpol = combinator.restore(allShares, lagrange);
        if (!mInterpol.isPresent()) {
            throw new InsufficientInformationError(
                    "Cannot restore secret: Redundant or missing information");
        }
        return decryptSecretWithItem(msg.getEncryptedSecret(), mInterpol.get());
    }
}
