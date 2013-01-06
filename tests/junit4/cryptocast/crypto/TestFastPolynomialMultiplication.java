package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestFastPolynomialMultiplication {
    @Test
    public void fft() throws Exception {
        List<BigInteger> a = ImmutableList.of(
                BigInteger.valueOf(1),
                BigInteger.valueOf(2),
                BigInteger.valueOf(3));
        List<BigInteger> b = ImmutableList.of(
                BigInteger.valueOf(4),
                BigInteger.valueOf(5));
        BigInteger mod = BigInteger.valueOf(3);
        System.out.println(FastPolynomialMultiplication.multiply(a, b, mod));
    }
}
