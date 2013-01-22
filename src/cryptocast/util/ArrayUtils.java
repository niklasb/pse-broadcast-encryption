package cryptocast.util;

/**
 * Provides copy of a specific range functionality for arrays.
 */
public class ArrayUtils {
    // this is in fact Arrays.copyOfRange from OpenJDK, but it was 
    // not included in Android API level < 9
    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }
    
    public static boolean equals(byte[] a1, byte[] a2) {
        if (a1.length != a2.length)
            return false;
        for (int i = 0; i < a1.length; ++i)
            if (a1[i] != a2[i])
                return false;
        return true;
    }
}
