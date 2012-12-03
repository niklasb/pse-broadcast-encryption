package cryptocast.crypto;

import java.nio.ByteBuffer;

/**
 * A strategy to encrypt a single secret
 * @param Secret the type of the secret
 */
public interface Encryptor<Secret> {
    /**
     * @param secret the secret
     * @return The cipher text
     */
    public ByteBuffer encrypt(Secret secret);
}
