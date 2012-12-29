package cryptocast.crypto;

import java.nio.ByteBuffer;

import cryptocast.util.Packable;

public class DynamicCipherKeyUpdateMessage implements Packable {
    private byte[] encryptedKey, iv;
    
    public DynamicCipherKeyUpdateMessage(byte[] encryptedKey, byte[] iv) {
        this.encryptedKey = encryptedKey;
        this.iv = iv;
    }

    public byte[] getEncryptedKey() { return encryptedKey; }
    public byte[] getIv() { return iv; }

    @Override
    public int getMaxSpace() {
        return 8 + encryptedKey.length + iv.length;
    }

    @Override
    public void pack(ByteBuffer buf) {
        buf.putInt(encryptedKey.length);
        buf.put(encryptedKey);
        buf.putInt(iv.length);
        buf.put(iv);
    }

    public static DynamicCipherKeyUpdateMessage unpack(ByteBuffer buf) {
        int keyLength = buf.getInt();
        byte[] encryptedKey = new byte[keyLength];
        buf.get(encryptedKey);
        int ivLength = buf.getInt();
        byte[] iv = new byte[ivLength];
        buf.get(iv);
        return new DynamicCipherKeyUpdateMessage(encryptedKey, iv);
    }
}