package cryptocast.util;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class NativeUtils {
    public static byte[][] bigIntListToTwoComplements(List<BigInteger> bigInts) {
        int len = bigInts.size();
        byte[][] result = new byte[len][];
        Iterator<BigInteger> x = bigInts.iterator();
        for (int i = 0; i < len; ++i) {
            result[i] = x.next().toByteArray();
        }
        return result;
    }
    
    public static ImmutableList<BigInteger> twoComplementsToBigIntList(byte[][] twoComplements) {
        ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
        for (int i = 0, len = twoComplements.length; i < len; ++i) {
            builder.add(new BigInteger(twoComplements[i]));
        }
        return builder.build();
    }
}
