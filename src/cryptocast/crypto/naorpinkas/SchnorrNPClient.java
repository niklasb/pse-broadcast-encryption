package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.io.ByteArrayDataInput;

import cryptocast.crypto.*;
import cryptocast.util.ArrayUtils;

public class SchnorrNPClient extends NPClient<BigInteger, SchnorrGroup> {
    public SchnorrNPClient(NPKey<BigInteger, SchnorrGroup> key) {
        super(key);
    }

    @Override
    protected NPShare<BigInteger, SchnorrGroup> readShare(
                           ByteArrayDataInput in) {
        BigInteger i = new BigInteger(readBytes(in)),
                   grpi = new BigInteger(readBytes(in));
        return new NPShare<BigInteger, SchnorrGroup>(i, grpi, getGroup());
    }

    @Override
    protected byte[] decryptSecretWithItem(byte[] encryptedSecret,
                                           BigInteger item) {
        BigInteger xor = new BigInteger(encryptedSecret);
        byte[] bytes = item.xor(xor).toByteArray();
        return ArrayUtils.copyOfRange(bytes, 1, bytes.length);
    }
}
