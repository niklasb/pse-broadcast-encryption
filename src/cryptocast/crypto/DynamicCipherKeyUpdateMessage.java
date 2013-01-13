package cryptocast.crypto;

import java.nio.ByteBuffer;

import cryptocast.util.Packable;

/**
 * A message for dynamic updates of the cipher key.
 */
public class DynamicCipherKeyUpdateMessage implements Packable {
    private byte[] encryptedKey, iv;
    /**
     * Creates a message with the given values.
     * 
     * @param encryptedKey the encypted key.
     * @param iv
     */
    public DynamicCipherKeyUpdateMessage(byte[] encryptedKey, byte[] iv) {
        this.encryptedKey = encryptedKey;
        this.iv = iv;
    }

    /**
     * Returns the encypted key.
     * @return The encypted key.
     */
    public byte[] getEncryptedKey() { return encryptedKey; }
    /**
     * Returns the
     * @return The
     */
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

    /**
     * Unpacks the message from the given buffer.
     * 
     * @param buf A byte buffer. 
     * @return a dynamic cipher key update message instance.
     */
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