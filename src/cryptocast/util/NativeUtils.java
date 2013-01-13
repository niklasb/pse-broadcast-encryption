package cryptocast.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;

/**
 * Provides several utility methods for easier native code calls.
 */
public class NativeUtils {
    /**
     * Converts a big integer into a corresponding two-complement.
     * 
     * @param bigInts The big integer.
     * @return A two-complement.
     */
    public static byte[][] bigIntListToTwoComplements(List<BigInteger> bigInts) {
        int len = bigInts.size();
        byte[][] result = new byte[len][];
        Iterator<BigInteger> x = bigInts.iterator();
        for (int i = 0; i < len; ++i) {
            result[i] = x.next().toByteArray();
        }
        return result;
    }
    /**
     * Converts a two-complement into the corresponding big integer.
     * 
     * @param twoComplements The two-complement.
     * @return A big integer.
     */
    public static ImmutableList<BigInteger> twoComplementsToBigIntList(byte[][] twoComplements) {
        ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
        for (int i = 0, len = twoComplements.length; i < len; ++i) {
            builder.add(new BigInteger(twoComplements[i]));
        }
        return builder.build();
    }
    /**
     * Loads a native library, logs an error if loading failed.
     * 
     * @param lib The library to load.
     * @param log The logger to log the error.
     * @return <code>true</code> if the library could be loaded, <code>false</code> otherwise.
     */
    public static boolean tryToLoadNativeLibOrLogFailure(String lib, Logger log) {
        try {
            System.loadLibrary(lib);
            return true;
        } catch (Error e) {
            log.warn("Could not load native library `" + lib + "'. Using slower Java implementation");
            log.debug("Loading failed because of", e);
            return false;
        }
    }
}
