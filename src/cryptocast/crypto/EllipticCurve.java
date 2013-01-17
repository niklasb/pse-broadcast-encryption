package cryptocast.crypto;

import java.math.BigInteger;

import com.google.common.base.Optional;

public interface EllipticCurve<T, P> {
    public T getA();
    public T getB();
    public Field<T> getField();
    
    public static class AffinePoint<T> {
        private T x, y;
        public AffinePoint(T x, T y) {
            this.x = x;
            this.y = y;
        }
        public T getX() { return x; }
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
    
    public P getInfinity();
    public boolean isInfinity(P point);
    public Optional<AffinePoint<T>> getAffineCoords(P p);
    public P getPoint(T x, T y);
    
    public P negate(P a);
    public P add(P a, P b);
    public P subtract(P a, P b);
    public P twice(P a);
    public P multiply(P a, BigInteger k);
}
