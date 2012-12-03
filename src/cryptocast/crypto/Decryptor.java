package cryptocast.crypto;

import java.nio.ByteBuffer;

/**
 * A strategy to decrypt a single secret
 * @param Secret the type of the secret
 */
public interface Decryptor<Secret> {
    /**
     * @param cipher The encrypted secret
     * @return The decrypted secret
     */
    public Secret decrypt(ByteBuffer cipher);
}
