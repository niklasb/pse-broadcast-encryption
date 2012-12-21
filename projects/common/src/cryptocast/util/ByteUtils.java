package cryptocast.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ByteUtils {
    public static byte[] str2bytes(String str) {
        try {
            return str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("can never happen");
        }
    }
    
    public static BigInteger getBigInt(ByteBuffer buf) {
        int len = buf.getInt();
        byte[] bytes = new byte[len];
        buf.get(bytes);
        return new BigInteger(bytes);
    }

    public static void putBigInt(ByteBuffer buf, BigInteger i) {
        byte[] bytes = i.toByteArray();
        buf.putInt(bytes.length);
        buf.put(bytes);
    }
    
    public static byte[] partialBufferToBytes(ByteBuffer buf) {
        byte[] result = new byte[buf.position()];
        System.arraycopy(buf.array(), 0, result, 0, result.length);
        return result;
    }
}
