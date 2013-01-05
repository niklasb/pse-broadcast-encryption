package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestSubproductTree {
    private Field<BigInteger> mod11 = new IntegersModuloPrime(BigInteger.valueOf(11));
    
    @Test
    public void isComputedCorrectly() throws Exception {
        SubproductTree<BigInteger> tree = SubproductTree.buildPow2(mod11, ImmutableList.of(
                BigInteger.valueOf(1),
                BigInteger.valueOf(2),
                BigInteger.valueOf(3),
                BigInteger.valueOf(4)));
        assertEquals(makePolynomial(mod11, new int[] { 2, 5, 2, 1, 1 }), tree.getRoot());
    }
    
    @Test
    public void canEvaluatePolynomial() throws Exception {
        SubproductTree<BigInteger> tree = SubproductTree.buildPow2(mod11, ImmutableList.of(
                BigInteger.valueOf(1),
                BigInteger.valueOf(2),
                BigInteger.valueOf(3),
                BigInteger.valueOf(4)));
        Polynomial<BigInteger> poly = makePolynomial(mod11, new int[] { 2, 3, 5, 7 });
        ImmutableList<BigInteger> expected = ImmutableList.of(
                BigInteger.valueOf(6),
                BigInteger.valueOf(7),
                BigInteger.valueOf(3),
                BigInteger.valueOf(3));
        assertEquals(expected, tree.evaluate(poly));
    }

    @Test
    public void randomTests() throws Exception {
        Random rnd = new Random();
        for (int i = 0; i < 10; ++i) {
            int points = rnd.nextInt(10) + 1;
            randomTest(rnd, mod11, rnd.nextInt(points), points);
        }
    }
    
    private void randomTest(Random rnd, Field<BigInteger> field, int polySize, int pointExp) {
        Polynomial<BigInteger> poly = Polynomial.createRandomPolynomial(rnd, field, polySize);
        ImmutableList.Builder<BigInteger> points = ImmutableList.builder();
        ImmutableList.Builder<BigInteger> expectedValues = ImmutableList.builder();
        for (int i = 0; i < (1 << pointExp); ++i) {
            BigInteger x = field.randomElement(rnd);
            points.add(x);
            expectedValues.add(poly.evaluate(x));
        }
        SubproductTree<BigInteger> tree = SubproductTree.buildPow2(field, points.build());
        assertEquals(expectedValues.build(), tree.evaluate(poly));
    }

    private Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        ImmutableList.Builder<BigInteger> coeff = ImmutableList.builder();
        for (int i = 0; i < coefficients.length; ++i) {
            coeff.add(BigInteger.valueOf(coefficients[i]));
        }
        return new Polynomial<BigInteger>(field, coeff.build());
    }
}
