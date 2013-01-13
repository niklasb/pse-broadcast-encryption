package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.util.NativeUtils;

/**
 * The field $\mathbb{Z}/p\mathbb{Z}$ of integers modulo a prime $p$.
 */
public class IntegersModuloPrime extends Field<BigInteger> 
                                 implements Serializable {
    private static final long serialVersionUID = -2607220779180962188L;
    private static final Logger log = LoggerFactory
            .getLogger(IntegersModuloPrime.class);

    private BigInteger p;

    private static boolean haveNative = 
            NativeUtils.tryToLoadNativeLibOrLogFailure("IntegersModuloPrime", log);

    /**
     * Initializes the field.
     * @param p A prime number
     */
    public IntegersModuloPrime(BigInteger p) {
        this.p = p;
    }

    /** @return $p$ */
    public BigInteger getP() {
        return p;
    }

    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b).mod(p);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b).mod(p);
    }

    @Override
    public BigInteger negate(BigInteger a) {
        return a.negate().mod(p);
    }

    @Override
    public BigInteger invert(BigInteger a) throws ArithmeticException {
        return a.modInverse(p);
    }

    @Override
    public BigInteger zero() {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger one() {
        return BigInteger.ONE;
    }
    
    @Override
    public BigInteger two() {
        return BigInteger.valueOf(2).mod(p);
    }
    
    @Override
    public BigInteger three() {
        return BigInteger.valueOf(3).mod(p);
    }
    
    @Override
    public BigInteger four() {
        return BigInteger.valueOf(4).mod(p);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b).mod(p);
    }

    @Override
    public BigInteger pow(BigInteger a, BigInteger e) {
        if (haveNative) {
            return new BigInteger(nativeModPow(a.toByteArray(), e.toByteArray(), getP().toByteArray()));
        } else {
            return a.modPow(e, p);
        }
    }

    private static native byte[] nativeModPow(byte[] x, byte[] e, byte[] m);
    
    @Override
    public BigInteger randomElement(Random rnd) {
        BigInteger r;
        // TODO improve performance. Check out 
        // http://stackoverflow.com/questions/2290057/how-to-generate-a-random-biginteger-value-in-java
        do {
            r = new BigInteger(p.bitLength(), rnd);
        } while (r.compareTo(p) >= 0);
        return r;
    }
    
    @Override
    public boolean isZero(BigInteger a) {
        return a.signum() == 0;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) { return false; }
        return p.equals(((IntegersModuloPrime)other).p);
    }

    /**
     * Returns the maximum space.
     * @return the maximum space.
     */
    public int getMaxNumberSpace() {
        // round up to next int: (a + b - 1) / b = ceil(a / b)
        // also add 4 bytes for size information and 1 byte for the sign bit
        return 4 + 1 + (getP().bitLength() + 7) / 8;
    }
}
