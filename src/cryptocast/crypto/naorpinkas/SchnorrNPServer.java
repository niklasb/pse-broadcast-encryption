package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.base.Optional;
import com.google.common.io.ByteArrayDataOutput;

import cryptocast.crypto.*;

public class SchnorrNPServer extends NPServer<BigInteger, SchnorrGroup> {
    private static final long serialVersionUID = 1656507589897819277L;

    protected SchnorrNPServer(NPServerContext<BigInteger, SchnorrGroup> ctx) {
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

    @Override
    protected void putShare(ByteArrayDataOutput out, 
                            NPShare<BigInteger, SchnorrGroup> share) {
        putBytes(out, share.getI().toByteArray());
        putBytes(out, share.getGRPI().toByteArray());
    }
}
