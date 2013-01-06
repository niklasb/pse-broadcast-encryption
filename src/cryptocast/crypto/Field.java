package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

/**
 * Represents a field over values of type T.
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
     * @return The zero element of the field
     */
    public abstract T zero();
    /**
     * @return The one element of the field
     */
    public abstract T one();
    
    public T two() {
        return add(one(), one());
    }
    
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
     * Raises an element of the field to an integer power.
     * @param a The element of the field
     * @param e The exponent
     * @return The value $a^e$
     */
    public T pow(T a, BigInteger e) {
        T result = one();
        for (BigInteger i = BigInteger.valueOf(0); 
                i.compareTo(e) < 0; 
                i = i.add(BigInteger.valueOf(1)))
            result = multiply(result, a);
        return result;
    }
}
