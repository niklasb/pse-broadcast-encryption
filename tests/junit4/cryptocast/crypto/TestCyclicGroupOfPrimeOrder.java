package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

class TestGroup extends CyclicGroupOfPrimeOrder<BigInteger> {
    private static final long serialVersionUID = 1L;
    private BigInteger p;
    
    public TestGroup(SchnorrGroup s) {
        super(s.getGenerator(), s.getOrder());
        this.p = s.getP();
    }
    
    @Override
    public BigInteger combine(BigInteger a, BigInteger b) {
        return a.multiply(b).mod(p);
    }

    @Override
    public BigInteger invert(BigInteger a) {
        return a.modInverse(p);
    }

    @Override
    public BigInteger identity() {
        return BigInteger.ONE;
    }
    
    public BigInteger getP() { return p; }
}

public class TestCyclicGroupOfPrimeOrder {
    private SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
    private TestGroup sut = new TestGroup(schnorr);
    private BigInteger p = sut.getP();
    private BigInteger x = schnorr.getFieldModP().randomElement(new Random()),
                       y = schnorr.getFieldModP().randomElement(new Random());
    
    @Test
    public void pow() throws Exception {
        assertEquals(x.modPow(y, p), sut.pow(x, y));
        assertEquals(x, sut.pow(x, BigInteger.ONE));
        assertEquals(BigInteger.ONE, sut.pow(x, BigInteger.ZERO));
    }
    
    @Test
    public void shamir() throws Exception {
        testShamir(x, BigInteger.valueOf(123123), 
                   y, new BigInteger("12736128937612378123"), 
                   sut);
        testShamir(BigInteger.ONE, BigInteger.ZERO, 
                   BigInteger.ONE, BigInteger.ZERO, 
                   sut);
        testShamir(BigInteger.ONE, BigInteger.ONE, 
                   BigInteger.ONE, BigInteger.ONE, 
                   sut);
    }
    
    @Test
    public void shamirWithList() throws Exception {
        testShamir(ImmutableList.<BigInteger>of(),
                   ImmutableList.<BigInteger>of(),
                   sut);
        testShamir(ImmutableList.<BigInteger>of(x),
                   ImmutableList.<BigInteger>of(y),
                   sut);
        testShamir(ImmutableList.of(x, y, BigInteger.valueOf(3), x),
                   ImmutableList.of(y, BigInteger.valueOf(1231231), x, y),
                   sut);
    }
    
    @Test
    public void multiexpShamir() throws Exception {
        testMultiexpShamir(ImmutableList.of(x, y),
                           ImmutableList.of(y, x),
                           1,
                           sut);
        testMultiexpShamir(ImmutableList.of(x, y, x, y, x, y, y),
                           ImmutableList.of(y, x, y, x, x, y, y),
                           2,
                           sut);
    }
    
    public static <T> void testShamir(T x, BigInteger k, T y, BigInteger l, 
                                      CyclicGroupOfPrimeOrder<T> group) {
        assertEquals(group.combine(group.pow(x, k), group.pow(y, l)),
                     group.shamir(x, k, y, l));
    }
    
    public static <T> void testShamir(List<T> b, List<BigInteger> e, 
                                      CyclicGroupOfPrimeOrder<T> group) {
        T res = group.identity();
        for (int i = 0; i < b.size(); ++i) {
            res = group.combine(res, group.pow(b.get(i), e.get(i)));
        }
        assertEquals(res, group.shamir(b, e));
    }
    
    public static <T> void testMultiexpShamir(List<T> b, List<BigInteger> e, 
                                              int k,
                                              CyclicGroupOfPrimeOrder<T> group) {
        T res = group.identity();
        for (int i = 0; i < b.size(); ++i) {
            res = group.combine(res, group.pow(b.get(i), e.get(i)));
        }
        assertEquals(res, group.multiexpShamir(b, e, k));
    }
    
    public static <T> void testMultiexp(List<T> b, List<BigInteger> e, 
                                        CyclicGroupOfPrimeOrder<T> group) {
        T res = group.identity();
        for (int i = 0; i < b.size(); ++i) {
            res = group.combine(res, group.pow(b.get(i), e.get(i)));
        }
        assertEquals(res, group.multiexp(b, e));
    }
}
