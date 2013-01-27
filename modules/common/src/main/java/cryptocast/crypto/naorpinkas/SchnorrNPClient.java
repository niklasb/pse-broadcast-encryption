package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.io.ByteArrayDataInput;

import cryptocast.crypto.*;

/** A client of the NP variant that uses a Schnorr group. */
public class SchnorrNPClient extends NPClient<BigInteger, SchnorrGroup> {
    /**
     * Initializes an instance.
     * @param key The personal key of the user
     */
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
    protected byte[] decryptSecretWithItem(byte[] encryptedSecret, BigInteger item) 
                               throws DecryptionError {
        return CryptoUtils.decryptAndHash(encryptedSecret, item.toByteArray());
    }
}
