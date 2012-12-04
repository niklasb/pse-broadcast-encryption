package cryptocast.crypto;

import java.nio.ByteBuffer;

/**
 * A strategy to decrypt a single secret
 * @param <S> the type of the secret
 */
public interface Decryptor<S> {
    /**
     * @param cipher The encrypted secret
     * @return The decrypted secret
     */
    public S decrypt(ByteBuffer cipher);
}
