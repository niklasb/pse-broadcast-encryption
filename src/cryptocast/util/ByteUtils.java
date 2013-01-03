package cryptocast.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.google.common.collect.ImmutableList;

import static cryptocast.util.ErrorUtils.cannotHappen;

public class ByteUtils {
    public static byte[] str2bytes(String str) {
        try {
            return str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            cannotHappen(e);
            return null; // just to make the compiler happy
        }
    }
    
    public static byte[] encodeUtf8(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            cannotHappen(e);
            return null; // just to make the compiler happy
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
    
    public static void putBigInts(ByteBuffer buf, List<BigInteger> lst) {
        buf.putInt(lst.size());
        for (BigInteger x : lst) {
            putBigInt(buf, x);
        }
    }
    
    public static ImmutableList<BigInteger> getBigInts(ByteBuffer buf) {
        int size = buf.getInt();
        ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
        for (int i = 0; i < size; ++i) {
            builder.add(getBigInt(buf));
        }
        return builder.build();
    }
    
    public static byte[] pack(Packable p) {
        ByteBuffer buf = ByteBuffer.allocate(p.getMaxSpace());
        buf.order(ByteOrder.BIG_ENDIAN);
        p.pack(buf);
        byte[] result = new byte[buf.position()];
        System.arraycopy(buf.array(), 0, result, 0, result.length);
        return result;
    }
    
    public static ByteBuffer startUnpack(byte[] data, int offset, int len) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.position(offset);
        buf.limit(offset + len);
        return buf;
    }
    
    public static ByteBuffer startUnpack(byte[] data) {
        return startUnpack(data, 0, data.length);
    }
}
