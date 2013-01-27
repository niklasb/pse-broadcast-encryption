package cryptocast.crypto;

import static cryptocast.util.ErrorUtils.cannotHappen;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Simple helper functions for symmetric encryption/decryption and hashing.
 */
public class CryptoUtils {
    private static byte[] aesEncryptDecrypt(int opmode, byte[] secret, byte[] key)
                                 throws BadPaddingException {
        Cipher cipher = null;
        try { cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); }
        catch (NoSuchPaddingException e) { cannotHappen(e); }
        catch (NoSuchAlgorithmException e) { cannotHappen(e); }

        byte[] rawKey = Arrays.copyOfRange(sha256(key), 0, 16);
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        try { cipher.init(opmode, skeySpec, new IvParameterSpec(new byte[16])); }
        catch (InvalidKeyException e) { cannotHappen(e); }
        catch (InvalidAlgorithmParameterException e) { cannotHappen(e); }

        try { return cipher.doFinal(secret); }
        catch (IllegalBlockSizeException e) { cannotHappen(e); return null; }
    }

    /**
     * Encrypts a plain text using AES-128 with the given key.
     * @param plain The plain text.
     * @param key The key.
     * @return The cipher text.
     */
    public static byte[] encrypt(byte[] plain, byte[] key) {
        try {
            return aesEncryptDecrypt(Cipher.ENCRYPT_MODE, plain, key);
        } catch (BadPaddingException e) {
            cannotHappen(e); return null;
        }
    }

    /**
     * Decrypts a cipher text using AES-128 with the given key.
     * @param cipher The cipher text.
     * @param key The key.
     * @return The plain text.
     * @throws DecryptionError in case of an error
     */
    public static byte[] decrypt(byte[] cipher, byte[] key)
                              throws DecryptionError {
        try {
            return aesEncryptDecrypt(Cipher.DECRYPT_MODE, cipher, key);
        } catch (BadPaddingException e) {
            throw new DecryptionError("Wrong key or corrupted ciphertext!");
        }
    }

    /**
     * Encrypts a plain text along with its SHA-256 hash using AES-128 with the given key.
     * @param plain The plain text.
     * @param key The key.
     * @return The cipher text.
     */
    public static byte[] encryptAndHash(byte[] plain, byte[] key) {
        byte[] plainWithHash = new byte[plain.length + 32];
        System.arraycopy(plain, 0, plainWithHash, 0, plain.length);
        System.arraycopy(sha256(plain), 0, plainWithHash, plain.length, 32);
        return encrypt(plainWithHash, key);
    }

    /**
     * Decrypts a cipher text that was encrypted using {@link encryptAndHash}
     * using the given key and verify the integrity of the plain text by
     * examining the supplied hash.
     * @param cipher The cipher text.
     * @param key The key.
     * @return The plain text.
     * @throws DecryptionError in case of an error (during decryption or hash
     * verification)
     */
    public static byte[] decryptAndHash(byte[] cipher, byte[] key)
                                 throws DecryptionError {
        byte[] plainWithHash = decrypt(cipher, key);
        if (plainWithHash.length < 32) {
            throw new DecryptionError("Wrong key or corrupted cipher text");
        }
        byte[] plain = Arrays.copyOfRange(plainWithHash, 0, plainWithHash.length - 32);
        byte[] hash = Arrays.copyOfRange(plainWithHash,
                             plainWithHash.length - 32, plainWithHash.length);
        if (!Arrays.equals(sha256(plain), hash)) {
            throw new DecryptionError("Wrong key or corrupted cipher text");
        }
        return plain;
    }

    /**
     * Computes the SHA-256 hash of the given input.
     * @param input The input data
     * @return The hash.
     */
    public static byte[] sha256(byte[] input) {
        MessageDigest md = null;
        try { md = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { cannotHappen(e); }
        md.update(input);
        return md.digest();
    }
}
