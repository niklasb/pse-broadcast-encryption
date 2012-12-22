package cryptocast.crypto.test;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import cryptocast.crypto.*;

public class TestLagrangeInterpolation {
//    @Test
//    public void interpolationInExponentWorks() {
//        ModularExponentiationGroup group = ModularExponentiationGroup.getP1024Q160();
//        Polynomial<BigInteger> poly = makePolynomial(group, new int[] { 2, 3, 7, 11 });
//        BigInteger[] xs = new BigInteger[] { 
//                BigInteger.valueOf(123123123),
//                BigInteger.valueOf(3),
//                BigInteger.valueOf(21233312),
//                BigInteger.valueOf(8888889)
//        };
//        BigInteger[] lambdas = LagrangeInterpolation.computeCoefficients(group, xs);
//        ModularExponentiationGroup outerGroup = new ModularExponentiationGroup(
//                group.getP().add(BigInteger.ONE), group.getG());
//        
//        BigInteger sum = group.zero();
//        BigInteger prod = group.one();
//        for (int i = 0; i < xs.length; ++i) {
//            BigInteger e = lambdas[i].multiply(poly.evaluate(xs[i]));
//            prod = prod.multiply(outerGroup.getPowerOfG(e));
//            sum = sum.add(e);
//        }
//        
//        BigInteger p0 = poly.evaluate(group.zero());
//        BigInteger expected = group.getPowerOfG(p0);
//        //assertEquals(expectedSum, actualSum);
//        assertEquals(group.getPowerOfG(p0), prod.mod(group.getP()));
//    }
//    
    @Test
    public void interpolationInExponentWorks() {
        SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
        Field<BigInteger> modQ = schnorr.getFieldModQ();
        Field<BigInteger> modP = schnorr.getFieldModP();
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 123123, 333333, 2222 });
        BigInteger[] xs = new BigInteger[] { 
                BigInteger.valueOf(99999999),
                BigInteger.valueOf(3123123),
                BigInteger.valueOf(22222222),
        };
        BigInteger[] lambdas = LagrangeInterpolation.computeCoefficients(modQ, xs);
        BigInteger prod = modP.one();
        for (int i = 0; i < xs.length; ++i) {
            BigInteger share = schnorr.getPowerOfG(poly.evaluate(xs[i]));
            BigInteger factor = modP.pow(share, lambdas[i]);
            prod = modP.multiply(prod, factor);
        }
        BigInteger p0 = poly.evaluate(modQ.zero());
        BigInteger expected = schnorr.getPowerOfG(p0);
        assertEquals(expected, prod);
    }

    @Test
    public void interpolationInSmallField() {
        testInterpolation(new IntegersModuloPrime(BigInteger.valueOf(67)),
                new int[] { 2, 3, 5, 65 },
                new BigInteger[] { 
                        BigInteger.valueOf(1),
                        BigInteger.valueOf(66),
                        BigInteger.valueOf(2),
                        BigInteger.valueOf(10),
                      });
    }

    @Test
    public void interpolationInLargeField() {
        testInterpolation(SchnorrGroup.getP1024Q160().getFieldModP(),
                new int[] { 2, 3, 7, 11, 13 },
                new BigInteger[] { 
                        BigInteger.valueOf(123123),
                        BigInteger.valueOf(33123),
                        BigInteger.valueOf(22233333),
                        BigInteger.valueOf(44411),
                        BigInteger.valueOf(22222222),
                      });
    }

    @Test
    public void randomTests() {
        Field<BigInteger> field = SchnorrGroup.getP1024Q160().getFieldModP();
        Random rnd = new Random();
        for (int i = 0; i < 10; ++i) {
            randomTest(10);
        }
    }

    @Test
    public void largeTest() {
        randomTest(40);
    }

    private void randomTest(int t) {
        Field<BigInteger> field = SchnorrGroup.getP1024Q160().getFieldModP();
        Random rnd = new Random();
        BigInteger[] xs = new BigInteger[t + 1];
        for (int j = 0; j < t + 1; ++j) {
            xs[j] = field.randomElement(rnd);
        }
        testInterpolationPoly(Polynomial.createRandomPolynomial(rnd, field, 10), xs);
    }

    private void testInterpolation(Field<BigInteger> field, int[] coeff, BigInteger[] xs) {
        testInterpolationPoly(makePolynomial(field, coeff), xs);
    }

    private void testInterpolationPoly(Polynomial<BigInteger> poly, BigInteger[] xs) {
        Field<BigInteger> field = poly.getField();
        BigInteger[] lambdas = LagrangeInterpolation.<BigInteger>computeCoefficients(field, xs);
        // we check the correctness by trying to interpolate $P(0)$
        BigInteger expected = poly.evaluate(field.zero());
        BigInteger actual = field.zero();
        for (int i = 0; i < xs.length; ++i) {
            actual = field.add(actual, 
                               field.multiply(poly.evaluate(xs[i]), 
                                              lambdas[i]));
        }
        assertEquals(expected, actual);
    }

    private Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        BigInteger[] coeff = new BigInteger[coefficients.length];
        for (int i = 0; i < coeff.length; ++i) {
            coeff[i] = BigInteger.valueOf(coefficients[i]);
        }
        return new Polynomial<BigInteger>(field, coeff);
    }
}
