package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import cryptocast.crypto.EllipticCurve.*;

import org.junit.Test;

import com.beust.jcommander.internal.Lists;

public class TestEllipticCurveOverFp {
    private BigInteger p = BigInteger.valueOf(11),
                       a = BigInteger.valueOf(2),
                       b = BigInteger.valueOf(5);
    EllipticCurveOverFp curve = new EllipticCurveOverFp(
         new IntegersModuloPrime(p), a, b);
    
    @Test
    public void negate() {
        Point<BigInteger> 
            point = curve.getPoint(BigInteger.valueOf(3), BigInteger.valueOf(4)),
            neg = curve.getPoint(BigInteger.valueOf(3), BigInteger.valueOf(7));
        assertEquals(neg, curve.negate(point));
        assertEquals(curve.getInfinity(), curve.negate(curve.getInfinity()));
    }
    
    @Test
    public void twice() {
        Point<BigInteger> 
            point = curve.getPoint(BigInteger.valueOf(3), BigInteger.valueOf(4)),
            twice = curve.getPoint(BigInteger.valueOf(8), BigInteger.valueOf(4)),
            fourTimes = curve.getPoint(BigInteger.valueOf(9), BigInteger.valueOf(2)),
            halfOfInf = curve.getPoint(BigInteger.valueOf(4), BigInteger.valueOf(0)),
            inf = curve.getInfinity();
        assertEquals(twice, curve.twice(point));
        assertEquals(fourTimes, curve.twice(twice));
        assertEquals(inf, curve.twice(inf));
        assertEquals(inf, curve.twice(halfOfInf));
    }
    
    @Test
    public void add() {
        Point<BigInteger> 
            p1 = curve.getPoint(BigInteger.valueOf(0), BigInteger.valueOf(4)),
            p2 = curve.getPoint(BigInteger.valueOf(0), BigInteger.valueOf(7)),
            p3 = curve.getPoint(BigInteger.valueOf(3), BigInteger.valueOf(4)),
            p4 = curve.getPoint(BigInteger.valueOf(8), BigInteger.valueOf(7)),
            p5 = curve.getPoint(BigInteger.valueOf(9), BigInteger.valueOf(2)),
            inf = curve.getInfinity();
        assertEquals(inf, curve.add(p1, p2));
        assertEquals(p4, curve.add(p1, p3));
        assertEquals(p4, curve.add(p4, inf));
        assertEquals(p4, curve.add(inf, p4));
        assertEquals(p5, curve.add(p1, p1));
    }
    
    @Test
    public void multiply() {
        Point<BigInteger> 
            p1 = curve.getPoint(BigInteger.valueOf(0), BigInteger.valueOf(4)),
            p2 = curve.getPoint(BigInteger.valueOf(0), BigInteger.valueOf(7)),
            p3 = curve.getPoint(BigInteger.valueOf(3), BigInteger.valueOf(4)),
            p4 = curve.getPoint(BigInteger.valueOf(8), BigInteger.valueOf(7)),
            p5 = curve.getPoint(BigInteger.valueOf(9), BigInteger.valueOf(2)),
            inf = curve.getInfinity();
        assertEquals(inf, curve.multiply(inf, BigInteger.ZERO));
        assertEquals(inf, curve.multiply(inf, BigInteger.valueOf(1000)));
        assertEquals(p3, curve.multiply(p1, BigInteger.valueOf(3)));
        assertEquals(inf, curve.multiply(p2, BigInteger.valueOf(10)));
        assertEquals(p4, curve.multiply(p5, BigInteger.valueOf(2)));
        assertEquals(p3, curve.multiply(p3, BigInteger.valueOf(1)));
        assertEquals(inf, curve.multiply(p3, BigInteger.ZERO));
        assertEquals(p2, curve.multiply(p3, BigInteger.valueOf(3)));
    }
    
    @Test
    public void largeMultiplyTest() {
        EllipticCurveGroup<BigInteger, EllipticCurveOverFp> group = 
                EllipticCurveGroup.getNamedCurve("prime192v1");
        EllipticCurveOverFp curve = group.getCurve();
        BigInteger k = new BigInteger("333333333333333333333333333333337c964c53", 16);
        Point<BigInteger> 
            actual = curve.multiply(group.getBasePoint(), k),
            expected = curve.getPoint(
                    new BigInteger("ccb5412903ebb05c8bf1672ff5b21ba7708537d06b5d68e8", 16),
                    new BigInteger("de2e4e3badcabdd2567d53df9fe1df9913894fa3359c620c", 16));
        assertEquals(expected, actual);
    }
    
    @Test
    public void multiexpWorks() {
        EllipticCurveGroup<BigInteger, EllipticCurveOverFp> group = 
                EllipticCurveGroup.getNamedCurve("secp160r1");
        List<Point<BigInteger>> bases = Lists.newArrayList();
        List<BigInteger> exps = Lists.newArrayList();
        bases.add(group.getBasePoint());
        bases.add(group.identity());
        bases.add(group.getPowerOfG(new BigInteger("fffffffffffff213", 16)));
        Random rnd = new Random();
        for (int i = 0; i < bases.size(); ++i) {
            exps.add(SchnorrGroup.getP1024Q160().getFieldModP().randomElement(rnd));
        }
        TestCyclicGroupOfPrimeOrder.testMultiexp(bases, exps, group);
    }
}
