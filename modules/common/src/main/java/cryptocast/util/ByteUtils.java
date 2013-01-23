package cryptocast.util;

import java.io.UnsupportedEncodingException;

import static cryptocast.util.ErrorUtils.cannotHappen;

/**
 * Several bytes and byte arrays utility funtions.
 */
public class ByteUtils {
    /**
     * Converts a string to byte array.
     * 
     * @param str The string.
     * @return A byte array.
     */
    public static byte[] str2bytes(String str) {
        try {
            return str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            cannotHappen(e);
            return null; // just to make the compiler happy
        }
    }
    
    /**
     * Encodes a string to utf-8 byte array.
     * 
     * @param str The string.
     * @return A utf-8 byte array.
     */
    public static byte[] encodeUtf8(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            cannotHappen(e);
            return null; // just to make the compiler happy
        }
    }
}
