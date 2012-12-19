package cryptocast.crypto;

import java.io.FilterOutputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ControllableCipherOutputStream extends FilterOutputStream
         implements OutputCipherControl {
    private OutputStream inner;
    private SecretKey key;
    private int keyBits;

    private ControllableCipherOutputStream(OutputStream inner, 
                                           int keyBits) {
        super(null);
        this.inner = inner;
        this.keyBits = keyBits;
        updateKey();
        reinitializeCipher();
    }
    
    public static ControllableCipherOutputStream setup(OutputStream inner,
                                                        int keyBits) {
        return new ControllableCipherOutputStream(inner, keyBits);
    }

    @Override
    public SecretKey getKey() {
        return key;
    }

    @Override
    public void updateKey() {
        key = createNewKey();
    }
    
    @Override
    public void reinitializeCipher() {
        this.out = createNewCipherStream(key);
    }

    private SecretKey createNewKey() {
        byte[] keyBytes = "abasdasd".getBytes();
        return new SecretKeySpec(keyBytes, "AES");
    }
    
    private OutputStream createNewCipherStream(SecretKey key) {
        byte[] ivBytes = new byte[] { 
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
                0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        } catch (Exception e) {
            // cannot happen: string is correct and AES must be supported.
            e.printStackTrace();
            System.exit(1);
        }
        return new CipherOutputStream(inner, cipher);
    }
}
