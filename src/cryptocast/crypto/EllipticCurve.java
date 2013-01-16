package cryptocast.crypto;

import java.math.BigInteger;

public abstract class EllipticCurve<T> {
    protected T a, b;
    
    public T getA() { return a; }
    public T getB() { return b; }
    public abstract Field<T> getField();

    public static interface Point<T> {}
    
    private static class Infinity<T> implements Point<T> {
        @Override
        public boolean equals(Object other) {
            return other != null && getClass() == other.getClass();
        }
    }
    
    public boolean isInfinity(Point<T> point) {
        return point instanceof Infinity<?>;
    }
    
    public Infinity<T> getInfinity() {
        return new Infinity<T>();
    }
    
    public static class ConcretePoint<T> implements Point<T> {
        private T x, y;
        private ConcretePoint(T x, T y) {
            this.x = x;
            this.y = y;
        }
        public T getX() { return x; }
        public T getY() { return y; }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != getClass()) { return false; }
            ConcretePoint<T> otherPoint;
            try {
                otherPoint = (ConcretePoint<T>) other;
            } catch (Throwable e) { return false; }
            return x.equals(otherPoint.x) && y.equals(otherPoint.y);
        }
    }
    
    public abstract Point<T> negate(Point<T> a);
    public abstract Point<T> add(Point<T> a, Point<T> b);
    
    public Point<T> subtract(Point<T> a, Point<T> b) {
        if (isInfinity(b)) { return a; }
        return add(a, negate((ConcretePoint<T>) b));
    }
    
    public Point<T> twice(Point<T> a) {
        return add(a, a);
    }
    
    public ConcretePoint<T> getPoint(T x, T y) {
        return new ConcretePoint<T>(x, y);
    }
    
    public abstract Point<T> multiply(Point<T> a, BigInteger k);
}
