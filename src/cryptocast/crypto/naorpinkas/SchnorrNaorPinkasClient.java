package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.InvalidProtocolBufferException;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.Protos.*;
import cryptocast.util.ArrayUtils;
import cryptocast.util.ErrorUtils;

public class SchnorrNaorPinkasClient extends NaorPinkasClient<BigInteger> {
    public SchnorrNaorPinkasClient(NaorPinkasPersonalKey<BigInteger> key) {
        super(key);
    }

    /**
     * Decrypts a secret.
     * @param cipher The encrypted secret.
     * @return The decrypted secret.
     */
    public byte[] decrypt(byte[] cipher) throws DecryptionError {
        NaorPinkasMessageSchnorr msg = null;
        try {
            msg = NaorPinkasMessageSchnorr.parseFrom(cipher);
        } catch (InvalidProtocolBufferException e) {
            ErrorUtils.throwWithCause(new DecryptionError("Could not parse protobuf!"), e);
        }
        int t = msg.getCommon().getT();
        ImmutableList.Builder<NaorPinkasShare<BigInteger>> shares = ImmutableList.builder();
        for (NaorPinkasShareSchnorr share : msg.getSharesList()) {
            BigInteger r = unpackBigInt(share.getR()),
                       i = unpackBigInt(share.getI()),
                       grpi = unpackBigInt(share.getGrpi());
            shares.add(new NaorPinkasShare<BigInteger>(t, r, i, grpi, getGroup()));
        }
        BigInteger decryptedItem = decryptItem(msg.getCommon(), shares.build()),
                   xor = new BigInteger(msg.getCommon().getEncryptedSecret().toByteArray()),
                   secretAsNum = decryptedItem.xor(xor);
        
        byte[] bytes = secretAsNum.toByteArray();
        return ArrayUtils.copyOfRange(bytes, 1, bytes.length);
    }
}
