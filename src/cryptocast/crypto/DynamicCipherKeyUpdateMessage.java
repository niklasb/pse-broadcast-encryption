package cryptocast.crypto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A message for dynamic updates of the cipher key.
 */
public class DynamicCipherKeyUpdateMessage {
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

    /**
     * Packs the message into a binary format that can be parsed
     * by the {@link unpack} method.
     * @return The packed message
     */
    public byte[] pack() {
        ByteBuffer buf = ByteBuffer.allocate(8 + iv.length + encryptedKey.length);
        buf.order(ByteOrder.BIG_ENDIAN);
        packByteArray(buf, encryptedKey);
        packByteArray(buf, iv);
        return buf.array();
    }
    
    /**
     * Parses a binary format created by {@link pack}.
     * @param the packed array
     * @return The parsed message
     */
    public static DynamicCipherKeyUpdateMessage unpack(byte[] packed) {
        ByteBuffer buf = ByteBuffer.wrap(packed);
        byte[] key = unpackByteArray(buf);
        byte[] iv = unpackByteArray(buf);
        return new DynamicCipherKeyUpdateMessage(key, iv);
    }
    
    private static void packByteArray(ByteBuffer buf, byte[] data) {
        buf.putInt(data.length);
        buf.put(data);
    }
    
    private static byte[] unpackByteArray(ByteBuffer buf) {
        int len = buf.getInt();
        byte[] data = new byte[len];
        buf.get(data);
        return data;
    }
}