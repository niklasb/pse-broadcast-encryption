package cryptocast.crypto;

import org.junit.Test;
import static org.junit.Assert.*;

import static cryptocast.util.ByteUtils.str2bytes;
import static cryptocast.crypto.CryptoUtils.*;

public class TestCryptoUtils {
    @Test
    public void encryptDecrypt() throws Exception {
        byte[] plain = str2bytes("aaasdas");
        byte[] key = str2bytes("foobar");
        assertArrayEquals(plain, decrypt(encrypt(plain, key), key));
    }
    
    @Test(expected = DecryptionError.class)
    public void encryptDecryptError() throws Exception {
        byte[] plain = str2bytes("aaasdas");
        assertArrayEquals(plain, decrypt(encrypt(plain, str2bytes("foobaz")), str2bytes("foobar")));
    }
    
    @Test
    public void encryptDecryptHash() throws Exception {
        byte[] plain = str2bytes("aaasdas");
        byte[] key = str2bytes("foobar");
        assertArrayEquals(plain, decryptAndHash(encryptAndHash(plain, key), key));
    }
    
    @Test(expected = DecryptionError.class)
    public void encryptDecryptHashWrongKey() throws Exception {
        byte[] plain = str2bytes("aaasdas");
        assertArrayEquals(plain, decryptAndHash(encryptAndHash(plain, str2bytes("foobaz")), 
                                                str2bytes("foobar")));
    }
    
    @Test(expected = DecryptionError.class)
    public void encryptDecryptHashWrongHash() throws Exception {
        byte[] plain = str2bytes("aaasdas");
        byte[] key = str2bytes("foobar");
        byte[] cipher = encryptAndHash(plain, key);
        cipher[cipher.length - 1] = 0;
        assertArrayEquals(plain, decryptAndHash(cipher, key));
    }
}
