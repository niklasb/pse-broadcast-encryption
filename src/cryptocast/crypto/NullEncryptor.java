package cryptocast.crypto;

public class NullEncryptor implements Encryptor<byte[]>, Decryptor<byte[]> {
    @Override
    public byte[] decrypt(byte[] cipher) throws DecryptionError {
        System.out.println("NullEncryptor.decrypt()");
        return cipher;
    }

    @Override
    public byte[] encrypt(byte[] secret) {
        return secret;
    }
}
