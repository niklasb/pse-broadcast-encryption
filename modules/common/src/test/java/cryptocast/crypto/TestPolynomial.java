package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestPolynomial {
    Field<BigInteger> mod11 = new IntegersModuloPrime(BigInteger.valueOf(11));
    Field<BigInteger> mod3 = new IntegersModuloPrime(BigInteger.valueOf(3));
    
	@Test
	public void degreeIsDetectedCorrectly() {
	    assertEquals(0, makePolynomial(mod11, new int[] { 0, 0, 0 }).getSize());
	    assertEquals(1, makePolynomial(mod11, new int[] { 1, 0, 0 }).getSize());
	    assertEquals(3, makePolynomial(mod11, new int[] { 1, 0, 1 }).getSize());
	}

	@Test
	public void evaluateWorks() {
	    Polynomial<BigInteger> poly = makePolynomial(mod11, new int[] { 2, 3, 5, 7 });
	    int[] values = new int[] { 2, 6, 7, 3, 3, 5, 7, 7, 3, 4, 8 };
	    for (int i = 0; i < values.length; ++i) {
	        assertEquals(BigInteger.valueOf(values[i]), poly.evaluate(BigInteger.valueOf(i)));
	    }
	}

	@Test
	public void evaluateMultiWorks() {
	    Polynomial<BigInteger> poly = makePolynomial(mod11, new int[] { 2, 3, 5, 7 });
	    int[] xs = { 6, 7, 3, 8, 9, 0, 10, 5, 2, 1, 4 };
	    int[] ys = { 7, 7, 3, 3, 4, 2, 8,  5, 7, 6, 3 };
	    assertEquals(intArrayToBigIntList(ys), 
	                 poly.evaluateMulti(intArrayToBigIntList(xs)));
	}

	@Test
	public void randomPolynomialHasCorrectDegree() {
	    Random rnd = new Random();
	    for (int i = 0; i < 100; ++i) {
	        for (int size : new int[] { 0, 1, 2, 12 }) {
	            Polynomial<BigInteger> p = Polynomial.createRandomPolynomial(rnd, mod11, size);
	            assertEquals(size, p.getSize());
	        }
	    }
	}

	private ImmutableList<BigInteger> intArrayToBigIntList(int[] xs) {
	    ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
	    for (int x : xs) {
	        builder.add(BigInteger.valueOf(x));
	    }
	    return builder.build();
	}

	private Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        ImmutableList.Builder<BigInteger> coeff = ImmutableList.builder();
        for (int i = 0; i < coefficients.length; ++i) {
            coeff.add(BigInteger.valueOf(coefficients[i]));
        }
        return new Polynomial<BigInteger>(field, coeff.build());
    }
}