package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/** Represents the cyclic group structure of the set of points $kG$ generated
 * by a generator $G$, which is a point on an elliptic curve.
 * @param <T> The type of coordinates of the curves points.
 * @param <P> The type of points of the curve.
 * @param <C> The type of the curve.
 */
public class EllipticCurveGroup<T, P, C extends EllipticCurve<T, P>>
              extends CyclicGroupOfPrimeOrder<P>
              implements Serializable {
    private static final long serialVersionUID = 1276532022589756079L;

    private C curve;
    private P basePoint;
    private BigInteger basePointOrder;

    /** @return The curve. */
    public C getCurve() { return curve; }

    /** Generates a group from the given parameters.
     * @param curve The elliptic curve.
     * @param basePoint The generator point.
     * @param basePointOrder The order of the generator point.
     */
    public EllipticCurveGroup(C curve,
            P basePoint,
            BigInteger basePointOrder) {
        super(basePoint, basePointOrder);
        this.curve = curve;
        this.basePoint = basePoint;
        this.basePointOrder = basePointOrder;
    }

    @Override
    public P combine(P a, P b) {
        return curve.add(a, b);
    }

    @Override
    public P twice(P a) {
        return curve.twice(a);
    }

    @Override
    public P pow(P a, BigInteger k) {
        return curve.multiply(a, k);
    }

    @Override
    public P invert(P a) {
        return curve.negate(a);
    }

    @Override
    public P identity() {
        return curve.getInfinity();
    }

    /**
     * Uses Shamir's trick to get much better performance. Uses {@link List.subList}, so you'd
     * better give it {@link ImmutableLists}.
     */
    @Override
    public P multiexp(List<P> bases, List<BigInteger> exponents) {
        return multiexpShamir(bases, exponents, 5);
    }

    /**
     * @returns the named group <code>secp160r1</code>.
     */
    public static EllipticCurveGroup<BigInteger, EllipticCurveOverFp.Point, EllipticCurveOverFp>
                               getSecp160R1() {
        BigInteger p = new BigInteger("ffffffffffffffffffffffffffffffff7fffffff", 16),
                   a = new BigInteger("ffffffffffffffffffffffffffffffff7ffffffc", 16),
                   b = new BigInteger("1c97befc54bd7a8b65acf89f81d4d4adc565fa45", 16),
                   x = new BigInteger("4a96b5688ef573284664698968c38bb913cbfc82", 16),
                   y = new BigInteger("23a628553168947d59dcc912042351377ac5fb32", 16),
                   q = new BigInteger("100000000000000000001f4c8f927aed3ca752257", 16);
        EllipticCurveOverFp curve = new EllipticCurveOverFp(new IntegersModuloPrime(p), a, b);
        EllipticCurveOverFp.Point G = curve.getPoint(x, y);
        return new EllipticCurveGroup<BigInteger, EllipticCurveOverFp.Point, EllipticCurveOverFp>(curve, G, q);
    }
}
