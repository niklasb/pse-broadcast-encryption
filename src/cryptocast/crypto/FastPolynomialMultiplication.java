package cryptocast.crypto;

import java.math.BigInteger;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class FastPolynomialMultiplication {
    public static ImmutableList<BigInteger> multiply(
                List<BigInteger> a, List<BigInteger> b, BigInteger mod) {
        int n = a.size() - 1,
            m = b.size() - 1;
        BigInteger maxC = BigInteger.valueOf(Math.max(a.size(), b.size())).multiply(mod).multiply(mod);
        int k = maxC.bitLength() + 2;
        BigInteger x = BigInteger.ONE.shiftLeft(k);
        assert maxC.compareTo(x) < 0;
        BigInteger P = evaluate(a, x),
                   Q = evaluate(b, x);
        BigInteger R = P.multiply(Q);
        ImmutableList.Builder<BigInteger> result = ImmutableList.builder();
        for (int i = 0; i <= n + m; ++i) {
            BigInteger c = R.mod(x);
            result.add(c.mod(mod));
            R = R.subtract(c).shiftRight(k);
        }
        return result.build();
    }

    public static BigInteger evaluate(List<BigInteger> coeffs, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        for (int i = coeffs.size() - 1; i >= 0; --i) {
            result = coeffs.get(i).add(result.multiply(x));
        }
        return result;
    }
}