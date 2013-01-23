package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

import com.google.common.base.Optional;

/**
 * Represents a group over a subset $F$ of the values of type T.
 * @param <T> The values we work on.
 */
public abstract class Field<T> implements Serializable {
    private static final long serialVersionUID = -5044040558567213861L;

    /**
     * Adds two elements of the field.
     * @param a First element
     * @param b Second element
     * @return The value $a + b$
     */
    public abstract T add(T a, T b);
    
    /**
     * Multiplies two elements of the field.
     * @param a First element
     * @param b Second element
     * @return The value $a \cdot b$
     */
    public abstract T multiply(T a, T b);
    
    /**
     * @param x
     * @return $x \cdot x$
     */
    public T square(T x) {
        return multiply(x, x);
    }
    
    /**
     * @param a An element of the field
     * @return The additive inverse $-a$ of $a$
     */
    public abstract T negate(T a);
    
    /**
     * @param a An element of the field
     * @return The multiplicative inverse $a^{-1}$ of $a$
     * @throws ArithmeticException If a is the zero element.
     */
    public abstract T invert(T a) throws ArithmeticException;
    
    /**
     * @return The additive identity of the field.
     */
    public abstract T zero();
    
    /**
     * @return The multiplicative identity of the field.
     */
    public abstract T one();
    
    /**
     * @return 2 (in the context of the field)
     */
    public abstract T two();
    
    /**
     * @return 3 (in the context of the field)
     */
    public abstract T three();
    
    /**
     * @return 4 (in the context of the field)
     */
    public abstract T four();
    
    /**
     * @param rnd A randomness provider
     * @return A random element of the field
     */
    public abstract T randomElement(Random rnd);

    /**
     * Subtracts two elements of the field.
     * @param a First element
     * @param b Second element
     * @return The value $a - b$
     */
    public T subtract(T a, T b) {
        return add(a, negate(b));
    }
    
    /**
     * Divides two elements of the field.
     * @param a First element
     * @param b Second element
     * @return The value $\frac{a}{b}$
     */
    public T divide(T a, T b) {
        return multiply(a, invert(b));
    }
    
    /**
     * @param a
     * @return whether $a = 0$
     */
    public boolean isZero(T a) {
        return a.equals(zero());
    }
    
    /**
     * @oaram a
     * @return A square root of $a$ or absent if none exists
     */
    public abstract Optional<T> sqrt(T a);

    /**
     * Raises an element of the field to an integer power.
     * Uses a simple square-and-multiply algorithm in the default
     * implementation.
     * 
     * @param a The element of the field
     * @param e The exponent
     * @return The value $a^e$
     */
    public T pow(T a, BigInteger e) {
        T q = one();
        for (int i = 0, len = e.bitLength(); i < len; i++) {
            if (e.testBit(i)) {
                q = multiply(q, a);
            }
            a = square(a);
        }
        return q;
    }
}
