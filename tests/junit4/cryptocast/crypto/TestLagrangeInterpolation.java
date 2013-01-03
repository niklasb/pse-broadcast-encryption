package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class TestLagrangeInterpolation {
    @Test
    public void interpolationInExponentWorks() {
        SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
        Field<BigInteger> modQ = schnorr.getFieldModQ();
        Field<BigInteger> modP = schnorr.getFieldModP();
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 123123, 333333, 2222 });
        BigInteger[] xs = { 
                BigInteger.valueOf(99999999),
                BigInteger.valueOf(3123123),
                BigInteger.valueOf(22222222),
        };
        ImmutableList<BigInteger> lambdas = 
                LagrangeInterpolation.computeCoefficients(modQ, ImmutableList.copyOf(xs));
        BigInteger prod = modP.one();
        for (int i = 0; i < xs.length; ++i) {
            BigInteger share = schnorr.getPowerOfG(poly.evaluate(xs[i]));
            BigInteger factor = modP.pow(share, lambdas.get(i));
            prod = modP.multiply(prod, factor);
        }
        BigInteger p0 = poly.evaluate(modQ.zero());
        BigInteger expected = schnorr.getPowerOfG(p0);
        assertEquals(expected, prod);
    }

    @Test
    public void interpolationInSmallField() {
        Field<BigInteger> field = new IntegersModuloPrime(BigInteger.valueOf(67));
        Polynomial<BigInteger> poly = makePolynomial(field, new int[] { 2, 3, 5, 65 });
        LagrangeInterpolation<BigInteger> lagrange = LagrangeInterpolation.fromXs(field, 
                ImmutableList.of( 
                    BigInteger.valueOf(1),
                    BigInteger.valueOf(66),
                    BigInteger.valueOf(2),
                    BigInteger.valueOf(10)));
        testInterpolation(poly, lagrange);
    }

    @Test
    public void interpolationInLargeField() {
        Field<BigInteger> field = SchnorrGroup.getP1024Q160().getFieldModP();
        Polynomial<BigInteger> poly = makePolynomial(field, new int[] { 2, 3, 7, 11, 13 });
        LagrangeInterpolation<BigInteger> lagrange = LagrangeInterpolation.fromXs(field, 
                ImmutableList.of( 
                    BigInteger.valueOf(123123),
                    BigInteger.valueOf(33123),
                    BigInteger.valueOf(22233333),
                    BigInteger.valueOf(44411),
                    BigInteger.valueOf(22222222)));
        testInterpolation(poly, lagrange);
    }

    @Test
    public void dynamicAddingAndRemovingOfPointsWorks() {
        Field<BigInteger> field = SchnorrGroup.getP1024Q160().getFieldModQ();
        Polynomial<BigInteger> poly = makePolynomial(field, 
                new int[] { 2, 3, 7, 11, 13 });
        LagrangeInterpolation<BigInteger> lagrange = 
                new LagrangeInterpolation<BigInteger>(field);
        
        lagrange.addXs(ImmutableSet.of(
                BigInteger.valueOf(123123),
                BigInteger.valueOf(33123),
                BigInteger.valueOf(22233333),
                BigInteger.valueOf(44411),
                BigInteger.valueOf(22222222)
        ));
        lagrange.removeXs(ImmutableSet.of(
                BigInteger.valueOf(33123),
                BigInteger.valueOf(44411)
        ));
        lagrange.addXs(ImmutableSet.of(
                BigInteger.valueOf(44411),
                BigInteger.valueOf(99999)
        ));
        assertEquals(5, lagrange.getCoefficients().size());
        testInterpolation(poly, lagrange);
    }
    
    @Test
    public void interpolationByDataPointsAdaptsXs() {
        Field<BigInteger> field = SchnorrGroup.getP1024Q160().getFieldModQ();
        Polynomial<BigInteger> poly = makePolynomial(field, 
                new int[] { 2, 3, 5 });
        LagrangeInterpolation<BigInteger> lagrange = 
                new LagrangeInterpolation<BigInteger>(field);
        
        lagrange.addXs(ImmutableSet.of(
                BigInteger.valueOf(123123),
                BigInteger.valueOf(33123),
                BigInteger.valueOf(22233333)
        ));
        
        Map<BigInteger, BigInteger> dataPoints = ImmutableMap.of(
                BigInteger.valueOf(3), poly.evaluate(BigInteger.valueOf(3)),
                BigInteger.valueOf(123), poly.evaluate(BigInteger.valueOf(123)),
                BigInteger.valueOf(1), poly.evaluate(BigInteger.valueOf(1)));
        
        assertEquals(poly.evaluate(field.zero()), 
                                   lagrange.interpolateP0(dataPoints));
        
        assertEquals(ImmutableSet.of(
                        BigInteger.valueOf(1), 
                        BigInteger.valueOf(3), 
                        BigInteger.valueOf(123)),
                     lagrange.getCoefficients().keySet());
    }
    
    @Test
    public void randomTests() {
        for (int i = 0; i < 10; ++i) {
            randomTest(10);
        }
    }

    @Test
    public void largeTest() {
        randomTest(50);
    }

    private void randomTest(int t) {
        Field<BigInteger> field = SchnorrGroup.getP1024Q160().getFieldModQ();
        Random rnd = new Random();
        ImmutableList.Builder<BigInteger> xs = ImmutableList.builder();
        for (int j = 0; j < t; ++j) {
            xs.add(field.randomElement(rnd));
        }
        LagrangeInterpolation<BigInteger> lagrange = 
                LagrangeInterpolation.fromXs(field, xs.build());
        Polynomial<BigInteger> poly = Polynomial.createRandomPolynomial(rnd, field, t);
        testInterpolation(poly, lagrange);
    }
    
    private void testInterpolation(Polynomial<BigInteger> poly, 
                                   LagrangeInterpolation<BigInteger> lagrange) {
        // we check the correctness by trying to interpolate $P(0)$
        BigInteger expected = poly.evaluate(poly.getField().zero());
        BigInteger actual = lagrange.interpolateP0(poly.getEvaluator());
        assertEquals(expected, actual);
    }

    private Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        ImmutableList.Builder<BigInteger> coeff = ImmutableList.builder();
        for (int i = 0; i < coefficients.length; ++i) {
            coeff.add(BigInteger.valueOf(coefficients[i]));
        }
        return new Polynomial<BigInteger>(field, coeff.build());
    }
}
