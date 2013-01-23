package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import cryptocast.crypto.EllipticCurveOverFp.*;

import org.junit.Test;

import com.google.common.collect.Lists;

public class TestEllipticCurveOverFp {
    private BigInteger p = BigInteger.valueOf(11),
                       a = BigInteger.valueOf(2),
                       b = BigInteger.valueOf(5);
    EllipticCurveOverFp curve = new EllipticCurveOverFp(
         new IntegersModuloPrime(p), a, b);
    
    @Test
    public void negate() {
        Point point = curve.getPoint(BigInteger.valueOf(3), BigInteger.valueOf(4)),
              neg = curve.getPoint(BigInteger.valueOf(3), BigInteger.valueOf(7));
        assertEquals(neg, curve.negate(point));
        assertEquals(curve.getInfinity(), curve.negate(curve.getInfinity()));
    }
    
    @Test
    public void twice() {
        Point point = curve.getPoint(BigInteger.valueOf(3), BigInteger.valueOf(4)),
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
        Point
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
        Point
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
        EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp> group = 
                EllipticCurveGroup.getSecp160R1();
        EllipticCurveOverFp curve = group.getCurve();
        BigInteger k = new BigInteger("333333333333333333333333333333337c964c53", 16);
        Point actual = curve.multiply(group.getBasePoint(), k),
              expected = curve.getPoint(
                    new BigInteger("8db96f262ee7e04484a75e4b330210636f1c4554", 16),
                    new BigInteger("db66abe61619a3e871d23a6e11251c1778aba93", 16));
        assertEquals(expected, actual);
    }
    
    @Test
    public void multiexpWorks() {
        EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp> group = 
                EllipticCurveGroup.getSecp160R1();
        List<Point> bases = Lists.newArrayList();
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
