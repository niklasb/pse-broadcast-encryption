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

	@Test
	public void canMultiplyPolynomials() {
	    Polynomial<BigInteger> p = makePolynomial(mod11, new int[] { 2, 3, 5, 7 }),
	                           q = makePolynomial(mod11, new int[] { 1, 2, 3, 4, 5 }),
	                           prod = p.multiply(q);
	    assertEquals(makePolynomial(mod11, new int[] { 2, 7, 6, 1, 7, 1, 9, 2 }), prod);
	}
	
	@Test
    public void canAddPolynomials() {
        Polynomial<BigInteger> p = makePolynomial(mod11, new int[] { 2, 3, 5, 7 }),
                               q = makePolynomial(mod11, new int[] { 1, 2, 3, 4, 5 }),
                               sum = p.add(q);
        assertEquals(makePolynomial(mod11, new int[] { 3, 5, 8, 0, 5 }), sum);
    }
	
	@Test
    public void canSubtractPolynomials() {
        Polynomial<BigInteger> p = makePolynomial(mod11, new int[] { 2, 3, 5, 7 }),
                               q = makePolynomial(mod11, new int[] { 1, 2, 6, 4, 5 }),
                               diff = p.subtract(q);
        assertEquals(makePolynomial(mod11, new int[] { 1, 1, 10, 3, 6 }), diff);
    }
	
	@Test
	public void canCreateMonomial() {
	    Polynomial<BigInteger> mono = Polynomial.monomial(mod11, BigInteger.valueOf(2), 2);
	    assertEquals(makePolynomial(mod11, new int[] { 0, 0, 2 }), mono);
	}
	
	@Test
	public void reverse() {
	    assertEquals(makePolynomial(mod11, new int[] { 3, 2, 1 }),
	                 makePolynomial(mod11, new int[] { 1, 2, 3 }).reverse());
	    assertEquals(makePolynomial(mod11, new int[] { 0, 0, 3, 2, 1 }),
                     makePolynomial(mod11, new int[] { 1, 2, 3 }).reverse(4));
	}
	
	@Test
    public void divMod() {	    
	    testDivMod(
                makePolynomial(mod3, new int[] { 2, 1, 2, 1 }),
                makePolynomial(mod3, new int[] { 2, 1, 1 }));
	    testDivMod(
                makePolynomial(mod11, new int[] { 2, 1 }),
                makePolynomial(mod11, new int[] { 1 }));
	    testDivMod(
                makePolynomial(mod11, new int[] { 2, }),
                makePolynomial(mod11, new int[] { 1, 2, 3, 4 }));
	    testDivMod(
                makePolynomial(mod11, new int[] { 1 }),
                makePolynomial(mod11, new int[] { 1, 2 }));
	    testDivMod(
                makePolynomial(mod11, new int[] { 2, 3, 5, 7, 0, 2, 4 }),
                makePolynomial(mod11, new int[] { 6, 1, 2, 0, 3 }));
	    testDivMod(
                makePolynomial(mod11, new int[] { 2, 3, 5, 7, 0, 2, 4, 0, 6 }),
                makePolynomial(mod11, new int[] { 6, 1, 2, 0, 3 }));
    }
	
	@Test
    public void modPowerOfX() throws Exception {
        assertEquals(makePolynomial(mod11, new int[] { 1, 2 }),
                     makePolynomial(mod11, new int[] { 1, 2, 3, 4 }).modPowerOfX(2));
        assertEquals(makePolynomial(mod11, new int[0]),
                makePolynomial(mod11, new int[] { 1, 2, 3, 4 }).modPowerOfX(0));
        assertEquals(makePolynomial(mod11, new int[] { 3, 2, 3, 4 }),
                makePolynomial(mod11, new int[] { 3, 2, 3, 4 }).modPowerOfX(10));
    }
	
	@Test
	public void inversePolyModPowerOfX() throws Exception {
	    testInversePolyModPowerOfX(makePolynomial(mod11, new int[] { 1, 2, 3 }), 2);
	    testInversePolyModPowerOfX(makePolynomial(mod11, new int[] { 2, 0, 2, 2, 3 }), 3);
	    testInversePolyModPowerOfX(makePolynomial(mod11, new int[] { 10, 6, 3, 2, 10 }), 4);
	    testInversePolyModPowerOfX(makePolynomial(mod11, new int[] { 9, 0, 2, 2 }), 10);
	    testInversePolyModPowerOfX(makePolynomial(mod11, new int[] { 2, 0, 2, 2 }), 1);
	    testInversePolyModPowerOfX(makePolynomial(mod11, new int[] { 1, 0, 2, 2 }), 0);
	    testInversePolyModPowerOfX(makePolynomial(mod11, new int[] { 1 }), 2);
	}
	
	private void testInversePolyModPowerOfX(Polynomial<BigInteger> p, int k) {
        Polynomial<BigInteger> u = p.inversePolyModPowerOfX(k);
        assertEquals(Polynomial.zero(mod11),
                makePolynomial(p.getField(), new int[] { 1 }).subtract(p.multiply(u)).modPowerOfX(k));
        assertTrue(u.getDegree() < k);
    }
	
	private void testDivMod(Polynomial<BigInteger> a, Polynomial<BigInteger> b) {
        Polynomial.DivMod<BigInteger> divMod = a.divMod(b);
        assertEquals(a, b.multiply(divMod.div).add(divMod.mod));
        assertTrue(divMod.mod.getDegree() < b.getDegree());
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