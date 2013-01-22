package cryptocast.crypto;

import static cryptocast.util.ErrorUtils.cannotHappen;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cryptocast.util.ArrayUtils;

public class CryptoUtils {
    private static byte[] aesEncryptDecrypt(int opmode, byte[] secret, byte[] key)
                                 throws BadPaddingException {
        Cipher cipher = null;
        try { cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); } 
        catch (NoSuchPaddingException e) { cannotHappen(e); }
        catch (NoSuchAlgorithmException e) { cannotHappen(e); }
        
        byte[] rawKey = ArrayUtils.copyOfRange(sha256(key), 0, 16);
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        try { cipher.init(opmode, skeySpec, new IvParameterSpec(new byte[16])); }
        catch (InvalidKeyException e) { cannotHappen(e); }
        catch (InvalidAlgorithmParameterException e) { cannotHappen(e); }
        
        try { return cipher.doFinal(secret); } 
        catch (IllegalBlockSizeException e) { cannotHappen(e); return null; }
    }
    
    public static byte[] encrypt(byte[] plain, byte[] key) {
        try {
            return aesEncryptDecrypt(Cipher.ENCRYPT_MODE, plain, key);
        } catch (BadPaddingException e) {
            cannotHappen(e); return null;
        }
    }
    
    public static byte[] decrypt(byte[] cipher, byte[] key) 
                              throws DecryptionError {
        try {
            return aesEncryptDecrypt(Cipher.DECRYPT_MODE, cipher, key);
        } catch (BadPaddingException e) {
            throw new DecryptionError("Wrong key or corrupted ciphertext!");
        }
    }
    
    public static byte[] encryptAndHash(byte[] plain, byte[] key) {
        byte[] plainWithHash = new byte[plain.length + 32];
        System.arraycopy(plain, 0, plainWithHash, 0, plain.length);
        System.arraycopy(sha256(plain), 0, plainWithHash, plain.length, 32);
        return encrypt(plainWithHash, key);
    }
    
    public static byte[] decryptAndHash(byte[] cipher, byte[] key) 
                                 throws DecryptionError {
        byte[] plainWithHash = decrypt(cipher, key);
        if (plainWithHash.length < 32) {
            throw new DecryptionError("Wrong key or corrupted cipher text");
        }
        byte[] plain = ArrayUtils.copyOfRange(plainWithHash, 0, plainWithHash.length - 32);
        byte[] hash = ArrayUtils.copyOfRange(plainWithHash, 
                             plainWithHash.length - 32, plainWithHash.length);
        if (!ArrayUtils.equals(sha256(plain), hash)) {
            throw new DecryptionError("Wrong key or corrupted cipher text");
        }
        return plain;
    }
    
    public static byte[] sha256(byte[] input) {
        MessageDigest md = null;
        try { md = MessageDigest.getInstance("SHA-256"); } 
        catch (NoSuchAlgorithmException e) { cannotHappen(e); }
        md.update(input);
        return md.digest();
    }
}
