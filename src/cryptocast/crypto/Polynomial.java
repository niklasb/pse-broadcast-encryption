package cryptocast.crypto;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Random;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * A polynomial $P$ over a field
 * @param <T> The type of the field's elements
 */
public class Polynomial<T> implements Serializable {
    private static final long serialVersionUID = -1360253235247059379L;
    
    private Field<T> field;
    private T[] coefficients;
    int degree;

    /**
     * Initializes a polynomial.
     * @param field An instance of the field over which the polynomial is formed.
     * @param coefficients The coefficients $c_i$ of the polynomial ($0 \leq i \leq n$).
     * The polynomial is defined as $P(x) := \sum_{i=0}^n c_i x^i = c_0 + c_1
     * x + ... + c_n x^n$.
     */
    public Polynomial(Field<T> field, T[] coefficients) {
        this.field = field;
        this.coefficients = coefficients;
        degree = -1;
        for (int i = coefficients.length - 1; i >= 0; --i) {
            if (!coefficients[i].equals(field.zero())) {
                degree = i;
                break;
            }
        }
    }

    /**
     * @return The field associated with this polynomial.
     */
    public Field<T> getField() {
        return field;
    }

    /**
     * Evaluates the polynomial at a single point x.
     * @param x The point
     * @return P(x)
     */
    public T evaluate(T x) {
        T result = field.zero();
        for (int i = degree; i >= 0; --i) {
            result = field.add(coefficients[i], field.multiply(result, x));
        }
        return result;
    }

    /**
     * Evaluates the polynomial at multiple points.
     * @param xs The points $x_i$ to evaluate
     * @return The array a defined by $a_i := P(x_i)$.
     */
    public T[] evaluateMulti(T[] xs) {
        @SuppressWarnings("unchecked")
        T[] result = (T[])Array.newInstance(field.getElementClass(), xs.length);
        for (int i = 0, len = xs.length; i < len; ++i) {
            result[i] = evaluate(xs[i]);
        }
        return result;
    }

    /**
     * @param i The index of the coefficient to get ($0 \leq i \leq n$), where
     *          $n$ is the degree of the polynomial.
     * @return $c_i$
     */
    public T getCoefficient(int i) {
        return coefficients[i];
    }

    /**
     * @return The degree of the polynomial or -1 if the polynomial has
     *         no degree (which is the case only for the zero polynomial)
     */
    public int getDegree() {
        return degree;
    }

    /**
     * Generates a random polynomial over the field.
     * @param <T> The type of the field's elements over which the random polynomial is formed.
     * @param rnd The source of randomness
     * @param field An instance of the field over which the polynomial is formed.
     * @param degree The degree of the generated polynomial (can be -1, see getDegree())
     * @return The generated polynomial
     */
    public static <T> Polynomial<T> createRandomPolynomial(Random rnd, Field<T> field, int degree) {
        checkArgument(degree >= -1, "Degree must be >= -1, but is " + degree);
        // the cast is ugly, but okay here because we explicitely
        // initialize every element before accessing it again
        @SuppressWarnings("unchecked")
        T[] coefficients = (T[])Array.newInstance(field.getElementClass(), degree + 1);
        for (int i = 0; i <= degree; ++i) {
            coefficients[i] = field.randomElement(rnd);
        }
        if (degree >= 0) {
            do {
                coefficients[degree] = field.randomElement(rnd);
            } while (coefficients[degree].equals(field.zero()));
        }
        return new Polynomial<T>(field, coefficients);
    }
}
