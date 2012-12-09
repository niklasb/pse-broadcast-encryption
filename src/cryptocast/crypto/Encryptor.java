package cryptocast.crypto;

/**
 * A strategy to encrypt a single secret
 * @param <S> the type of the secret
 */
public interface Encryptor<S> {
    /**
     * Encrypts a secret
     * @param secret the secret
     * @return The cipher text
     */
    public byte[] encrypt(S secret);
}
