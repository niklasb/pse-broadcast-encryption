package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

public class TestPolynomial {

	@Test
	public void degreeIsDetectedCorrectly() {
	    assertEquals(-1, makePolynomialMod11(new int[] { 0, 0, 0 }).getDegree());
	    assertEquals(0, makePolynomialMod11(new int[] { 1, 0, 0 }).getDegree());
	    assertEquals(2, makePolynomialMod11(new int[] { 1, 0, 1 }).getDegree());
	}

	@Test 
	public void evaluateWorks() {
	    Polynomial<BigInteger> poly = makePolynomialMod11(new int[] { 2, 3, 5, 7 });
	    int[] values = new int[] { 2, 6, 7, 3, 3, 5, 7, 7, 3, 4, 8 };
	    for (int i = 0; i < values.length; ++i) {
	        assertEquals(BigInteger.valueOf(values[i]), poly.evaluate(BigInteger.valueOf(i)));
	    }
	}

	@Test 
	public void evaluateMultiWorks() {
	    Polynomial<BigInteger> poly = makePolynomialMod11(new int[] { 2, 3, 5, 7 });
	    int[] xs = new int[] { 6, 7, 3, 8, 9, 0, 10, 5, 2, 1, 4 };
	    int[] ys = new int[] { 7, 7, 3, 3, 4, 2, 8,  5, 7, 6, 3 };
	    assertArrayEquals(intArrayToBigIntArray(ys), 
	                      poly.evaluateMulti(intArrayToBigIntArray(xs)));
	}

	private BigInteger[] intArrayToBigIntArray(int[] xs) {
	    BigInteger[] result = new BigInteger[xs.length];
	    for (int i = 0; i < xs.length; ++i) {
	        result[i] = BigInteger.valueOf(xs[i]);
	    }
	    return result;
	}

	private Polynomial<BigInteger> makePolynomialMod11(int coefficients[]) {
	    BigInteger[] coeff = new BigInteger[coefficients.length];
	    for (int i = 0; i < coeff.length; ++i) {
	        coeff[i] = BigInteger.valueOf(coefficients[i]);
	    }
	    return new Polynomial<BigInteger>(new IntegersModuloPrime(BigInteger.valueOf(11)), coeff);
	}
}
