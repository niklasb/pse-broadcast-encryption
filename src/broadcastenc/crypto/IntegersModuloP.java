package broadcastenc.crypto;

import java.math.BigInteger;

/**
 * The field $Z/Zp$ of integers modulo a prime $p$
 */
public class IntegersModuloP extends Field<BigInteger> {
    /**
     * Initializes the field
     * @param p A prime number
     */
    public IntegersModuloP(BigInteger p) {}

    public BigInteger add(BigInteger a, BigInteger b) { return null; }
    public BigInteger multiply(BigInteger a, BigInteger b) { return null; }
    public BigInteger negate(BigInteger a) { return null; }
    public BigInteger invert(BigInteger a) throws ArithmeticException { return null; }
    public BigInteger zero() { return null; }
    public BigInteger one() { return null; }
    public BigInteger randomElement() { return null; }
}