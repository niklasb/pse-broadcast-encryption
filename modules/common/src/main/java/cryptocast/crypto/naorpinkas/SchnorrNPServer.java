package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.base.Optional;
import com.google.common.io.ByteArrayDataOutput;

import cryptocast.crypto.*;

/** The server of the NP variant the uses a Schnorr group. */
public class SchnorrNPServer extends NPServer<BigInteger, SchnorrGroup> {
    private static final long serialVersionUID = 1656507589897819277L;

    protected SchnorrNPServer(NPServerContext<BigInteger, SchnorrGroup> ctx) {
        super(ctx);
    }

    @Override
    protected Optional<byte[]> encryptSecretWithItem(byte[] secret, BigInteger key) {
        // check if secret is small enough to be properly encrypted.
        // subtract some space to be sure
        if (secret.length >= getContext().getGroup().getP().bitLength() / 8 - 2) {
            return Optional.absent();
        }
        return Optional.of(CryptoUtils.encryptAndHash(secret, key.toByteArray()));
    }

    @Override
    protected void writeShare(ByteArrayDataOutput out,
                              NPShare<BigInteger, SchnorrGroup> share) {
        putBytes(out, share.getI().toByteArray());
        putBytes(out, share.getGRPI().toByteArray());
    }
}
