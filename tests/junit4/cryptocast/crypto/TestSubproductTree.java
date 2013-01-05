package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;

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
        assertEquals(ImmutableList.of(0, 1, 3, 8), tree.evaluate(poly));
    }

    private Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        ImmutableList.Builder<BigInteger> coeff = ImmutableList.builder();
        for (int i = 0; i < coefficients.length; ++i) {
            coeff.add(BigInteger.valueOf(coefficients[i]));
        }
        return new Polynomial<BigInteger>(field, coeff.build());
    }
}
