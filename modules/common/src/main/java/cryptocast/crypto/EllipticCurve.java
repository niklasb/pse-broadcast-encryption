package cryptocast.crypto;

import java.math.BigInteger;

import com.google.common.base.Optional;

/**
 * Represents an elliptic curve in a field over values of type T.
 * @param <T> The type of values in the field
 * @param <P> The type of points of the elliptic curve
 */
public interface EllipticCurve<T, P> {
    /** @return the coefficient $a$ of the curve. */
    public T getA();
    /** @return the coefficient $b$ of the curve. */
    public T getB();
    /** @return the field of the elliptic curve. */
    public Field<T> getField();

    /**
     * Represents a point on the elliptic curve that is not the identity
     * element.
     * @param <T> The type of the coordinates
     */
    public static class AffinePoint<T> {
        private T x, y;
        /**
         * Creates an instance.
         * @param x The first coordinate of the point.
         * @param y The second coordinate of the point.
         */
        public AffinePoint(T x, T y) {
            this.x = x;
            this.y = y;
        }
        /** @return The first coordinate of the point. */
        public T getX() { return x; }
        /** @return The second coordinate of the point. */
        public T getY() { return y; }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != getClass()) { return false; }
            AffinePoint<T> otherPoint;
            try {
                otherPoint = (AffinePoint<T>) other;
            } catch (Throwable e) { return false; }
            return x.equals(otherPoint.x) && y.equals(otherPoint.y);
        }
    }

    /** @return the identity element of the curve. */
    public P getInfinity();

    /**
     * @param point a point on the curve.
     * @return whether <code>point</code> is the identity element.
     */
    public boolean isInfinity(P point);

    /**
     * @param p a point on the curve.
     * @return The affine representation of the point or absent if the point is
     * the identity element. */
    public Optional<AffinePoint<T>> getAffineCoords(P p);

    /**
     * Creates a point on the curve with the given coordinates.
     * @param x The first coordinate of the point.
     * @param y The second coordinate of the point.
     */
    public P getPoint(T x, T y);

    /**
     * @param a
     * @return The point $-a$.
     */
    public P negate(P a);

    /**
     * @param a
     * @param b
     * @return The point $a + b$.
     */
    public P add(P a, P b);

    /**
     * @param a
     * @param b
     * @return The point $a - b$.
     */
    public P subtract(P a, P b);

    /**
     * @param a
     * @return The point $a + a$.
     */
    public P twice(P a);

    /**
     * @param a
     * @param k
     * @return The point $k \cdot a$.
     */
    public P multiply(P a, BigInteger k);
}
