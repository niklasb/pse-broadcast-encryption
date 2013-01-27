package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;

import com.google.common.base.Optional;
import cryptocast.crypto.EllipticCurveOverFp.Point;

/**
 * An elliptic curve over $GF(p)$ where $p$ is prime.
 */
public class EllipticCurveOverFp implements EllipticCurve<BigInteger, Point>,
                                            Serializable {
    private static final long serialVersionUID = 3073796632806135558L;

    private IntegersModuloPrime field;
    private BigInteger a, b;
    private Point inf;

    /** A point on the curve. */
    // opaque type, points represented using jacobian coordinates.
    public static class Point implements Serializable {
        private static final long serialVersionUID = -4835674564581364941L;

        private BigInteger jx, jy, jz;
        private EllipticCurveOverFp curve;
        private Point(BigInteger jx, BigInteger jy, BigInteger jz, EllipticCurveOverFp curve) {
            this.jx = jx;
            this.jy = jy;
            this.jz = jz;
            this.curve = curve;
        }

        @Override
        public boolean equals(Object other_) {
            if (other_ == null || getClass() != other_.getClass()) { return false; }
            Point other = (Point) other_;
            return curve.getAffineCoords(this).equals(curve.getAffineCoords(other));
        }
    }

    private BigInteger _1, _2, _3, _4, _8;

    /**
     * @param field The field $GF(p)$
     * @param a The coefficient $a$ of the curve.
     * @param b The coefficient $b$ of the curve.
     */
    public EllipticCurveOverFp(IntegersModuloPrime field,
                               BigInteger a,
                               BigInteger b) {
        this.field = field;
        this.a = a;
        this.b = b;
        this.inf = new Point(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, this);
        _1 = field.one();
        _2 = field.two();
        _3 = field.three();
        _4 = field.four();
        _8 = field.add(_4, _4);
    }

    @Override
    public boolean isInfinity(Point point) {
        return point.jz.signum() == 0;
    }

    @Override
    public Point getInfinity() {
        return inf;
    }

    @Override
    public BigInteger getA() { return a; }
    @Override
    public BigInteger getB() { return b; }

    @Override
    public Point getPoint(BigInteger x, BigInteger y) {
        return new Point(x, y, _1, this);
    }

    @Override
    public IntegersModuloPrime getField() {
        return field;
    }

    @Override
    public Point negate(Point a) {
        if (isInfinity(a)) { return a; }
        return new Point(a.jx, field.negate(a.jy), a.jz, this);
    }

    @Override
    public Point add(Point p, Point q) {
        if (isInfinity(p)) { return q; }
        if (isInfinity(q)) { return p; }

        BigInteger Z12 = field.square(p.jz),
                   Z22 = field.square(q.jz),
                   U1 = field.multiply(p.jx, Z22),
                   U2 = field.multiply(q.jx, Z12),
                   S1 = field.multiply(p.jy, field.multiply(Z22, q.jz)),
                   S2 = field.multiply(q.jy, field.multiply(Z12, p.jz));
        if (U1.equals(U2)) {
            if (S1.equals(S2)) {
                return twice(p);
            } else {
                return inf;
            }
        }
        BigInteger H = field.subtract(U2, U1),
                   H2 = field.square(H), H3 = field.multiply(H, H2),
                   R = field.subtract(S2, S1),
                   X3 = field.subtract(field.subtract(field.square(R), H3),
                                       field.multiply(_2, field.multiply(U1, H2))),
                   Y3 = field.subtract(
                           field.multiply(
                              R,
                              field.subtract(
                                 field.multiply(U1, H2),
                                 X3)),
                           field.multiply(S1, H3)),
                   Z3 = field.multiply(field.multiply(H, p.jz), q.jz);
        return new Point(X3, Y3, Z3, this);
    }

    @Override
    public Point twice(Point p) {
        if (isInfinity(p) || p.jy.signum() == 0) { return inf; }
        BigInteger // S = 4*X*Y^2
                   S = field.multiply(_4, field.multiply(p.jx, field.square(p.jy))),
                   // M = 3*X^2 + a*Z^4
                   M = field.add(field.multiply(_3, field.square(p.jx)),
                                 field.multiply(a, field.pow(p.jz, BigInteger.valueOf(4)))),
                   // X' = M^2 - 2*S
                   XX = field.subtract(field.square(M), field.multiply(_2, S)),
                   // Y' = M*(S - X') - 8*Y^4
                   YY = field.subtract(field.multiply(M, field.subtract(S, XX)),
                                       field.multiply(_8, field.pow(p.jy, BigInteger.valueOf(4)))),
                   // Z' = 2*Y*Z
                   ZZ = field.multiply(_2, field.multiply(p.jy, p.jz));
        assert ZZ.signum() != 0;
        return new Point(XX, YY, ZZ, this);
    }

    @Override
    public Point multiply(Point p, BigInteger k) {
        if (k.signum() == 0) {
            return inf;
        }
        BigInteger h = k.multiply(BigInteger.valueOf(3));

        Point neg = negate(p), R = p;

        for (int i = h.bitLength() - 2; i > 0; --i) {
            R = twice(R);

            boolean hBit = h.testBit(i),
                    kBit = k.testBit(i);

            if (hBit != kBit) {
                R = add(R, hBit ? p : neg);
            }
        }

        return R;
    }

    /** A representation of a point compressed to the $x$ coordinate. */
    public static class CompressedPoint {
        private BigInteger x;
        private byte info;
        public CompressedPoint(BigInteger x, byte info) {
            this.x = x;
            this.info = info;
        }
        public BigInteger getX() { return x; }
        public byte getInfo() { return info; }
    }

    /**
     * @param p a point
     * @return The compressed represntation of $p$.
     */
    public CompressedPoint compress(Point p) {
        if (isInfinity(p)) {
            return new CompressedPoint(BigInteger.ZERO, (byte) 2);
        }
        AffinePoint<BigInteger> ap = getAffineCoords(p).get();
        byte info = (byte) (ap.getY().testBit(0) ? 1 : 0);
        return new CompressedPoint(ap.getX(), info);
    }

    /**
     * @param p a compressed point
     * @return The uncompressed representation of $p$.
     */
    public Point uncompress(CompressedPoint p) {
        if (p.getInfo() == (byte) 2) {
            return getInfinity();
        }
        BigInteger x = p.getX();
        BigInteger alpha =
                field.add(field.multiply(x, field.add(field.square(x), a)), b);
        Optional<BigInteger> mY = field.sqrt(alpha);
        if (!mY.isPresent()) {
            throw new ArithmeticException("Invalid compressed point!");
        }
        BigInteger y = mY.get();
        boolean ybyte = p.getInfo() != 0;
        if (y.testBit(0) == ybyte) {
            return getPoint(x, y);
        } else {
            return getPoint(x, field.negate(y));
        }
    }

    @Override
    public Optional<AffinePoint<BigInteger>> getAffineCoords(Point p) {
        if (isInfinity(p)) { return Optional.absent(); }
        BigInteger jz2 = field.multiply(p.jz, p.jz);
        return Optional.of(
            new AffinePoint<BigInteger>(
                field.divide(p.jx, jz2),
                field.divide(p.jy, field.multiply(jz2, p.jz))));
    }

    @Override
    public Point subtract(Point a, Point b) {
        return add(a, negate(b));
    }
}
