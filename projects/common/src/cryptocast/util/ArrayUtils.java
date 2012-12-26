package cryptocast.util;

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
}
