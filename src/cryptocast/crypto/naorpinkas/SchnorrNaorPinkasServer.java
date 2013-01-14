package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.base.Optional;

import cryptocast.crypto.naorpinkas.Protos.*;

public class SchnorrNaorPinkasServer extends NaorPinkasServer<BigInteger> {
    private static final long serialVersionUID = 1656507589897819277L;

    protected SchnorrNaorPinkasServer(
            NaorPinkasServerContext<BigInteger> ctx) {
        super(ctx);
    }

    @Override
    protected Optional<byte[]> encryptSecretWithItem(byte[] secret, BigInteger key) {
        byte[] bytes = new byte[secret.length + 1];
        // explicitly set the sign bit so we can safely remove it on the other side
        bytes[0] = 0x01;
        System.arraycopy(secret, 0, bytes, 1, secret.length);
        BigInteger secretAsNum = new BigInteger(bytes);
        if (secretAsNum.bitLength() > key.bitLength()) {
            return Optional.absent();
        }
        return Optional.of(secretAsNum.xor(key).toByteArray());
    }

    protected NaorPinkasMessageSchnorr encryptMessage(byte[] secret) {
        UnresolvedEncryptionMessage<BigInteger> partial = encryptPartial(secret);
        NaorPinkasMessageSchnorr.Builder builder = NaorPinkasMessageSchnorr.newBuilder();
        for (NaorPinkasShare<BigInteger> share : partial.shares) {
            builder.addShares(
                NaorPinkasShareSchnorr.newBuilder()
                    .setGrpi(packBigInt(share.getGRPI()))
                    .setI(packBigInt(share.getI()))
                    .setR(packBigInt(share.getR()))
                    .build());
        }
        builder.setCommon(partial.common);
        return builder.build();
    }

    @Override
    public byte[] encrypt(byte[] secret) {
        return encryptMessage(secret).toByteArray();
    }
}
