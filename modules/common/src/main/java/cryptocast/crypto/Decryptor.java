package cryptocast.crypto;

/**
 * A strategy to decrypt a single secret.
 * @param <S> the type of the secret
 */
public interface Decryptor<S> {
    /**
     * Decrypts a secret.
     * @param cipher The encrypted secret.
     * @return The decrypted secret.
     */
    public S decrypt(byte[] cipher) throws DecryptionError;
}
