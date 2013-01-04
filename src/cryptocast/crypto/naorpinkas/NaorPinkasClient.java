package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import cryptocast.util.ByteUtils;

import java.math.BigInteger;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * A client in the Naor-Pinkas broadcast encryption scheme.
 */
public class NaorPinkasClient implements Decryptor<byte[]> {
    private NaorPinkasPersonalKey key;
    private NaorPinkasShareCombinator combinator = new NaorPinkasShareCombinator();
    private SchnorrGroup schnorr;
    
    /**
     * Initializes a Naor-Pinkas broadcast client.
     * @param key The personal key used to reconstruct a secret from the stream.
     */
    public NaorPinkasClient(NaorPinkasPersonalKey key) {
        this.key = key;
        schnorr = key.getSchnorr();
    }

    /**
      * Decrypts a secret.
      * @param cipher The encrypted secret
      * @return The decrypted secret
      */
    public byte[] decrypt(byte[] cipher) throws InsufficientInformationError {
        NaorPinkasMessage msg = NaorPinkasMessage.unpack(
                ByteUtils.startUnpack(cipher));
        byte[] bytes = decryptNumber(msg).toByteArray();
        byte[] secret = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, secret, 0, secret.length);
        return secret;
    }
    
    public BigInteger decryptNumber(NaorPinkasMessage msg) throws InsufficientInformationError {
        BigInteger r = msg.getR();
        
        // make a mutable copy, so we can add our own share
        ImmutableList<NaorPinkasShare> shares = ImmutableList.<NaorPinkasShare>builder()
                .addAll(msg.getShares())
                .add(key.getShare(r, schnorr.getPowerOfG(r)))
                .build();
        Optional<BigInteger> mInterpol = combinator.restore(shares, msg.getLagrange());
        if (!mInterpol.isPresent()) {
            throw new InsufficientInformationError(
                    "Cannot restore secret: Redundant or missing information");
        }
        return mInterpol.get().xor(msg.getXor());
    }
}