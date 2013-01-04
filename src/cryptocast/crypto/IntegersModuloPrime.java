package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

/**
 * The field $\mathbb{Z}/p\mathbb{Z}$ of integers modulo a prime $p$.
 */
public class IntegersModuloPrime extends Field<BigInteger> 
                                 implements Serializable {
    private static final long serialVersionUID = -2607220779180962188L;
    
    private BigInteger p;
   
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
        return BigInteger.valueOf(0);
    }

    @Override
    public BigInteger one() {
        return BigInteger.valueOf(1);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b).mod(p);
    }

    @Override
    public BigInteger pow(BigInteger a, BigInteger e) {
        return a.modPow(e, p);
    }

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
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) { return false; }
        return p.equals(((IntegersModuloPrime)other).p);
    }

    public int getMaxNumberSpace() {
        // round up to next int: (a + b - 1) / b = ceil(a / b)
        // also add 4 bytes for size information and 1 byte for the sign bit
        return 4 + 1 + (getP().bitLength() + 7) / 8;
    }
}