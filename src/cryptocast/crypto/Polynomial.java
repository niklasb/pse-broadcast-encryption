package cryptocast.crypto;

import java.util.List;
import java.io.Serializable;
import java.util.Random;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A polynomial $P$ over a field.
 * @param <T> The type of the field's elements
 */
public class Polynomial<T> implements Serializable {
    private static final long serialVersionUID = -1360253235247059379L;
    
    private Field<T> field;
    private ImmutableList<T> coefficients;
    int size;

    /**
     * Initializes a polynomial.
     * @param field An instance of the field over which the polynomial is formed.
     * @param coefficients The coefficients $c_i$ of the polynomial ($0 \leq i \leq n$).
     * The polynomial is defined as $P(x) := \sum_{i=0}^n c_i x^i = c_0 + c_1
     * x + ... + c_n x^n$.
     */
    public Polynomial(Field<T> field, List<T> coefficients) {
        this.field = field;
        this.coefficients = ImmutableList.copyOf(coefficients);
        size = 0;
        for (int i = coefficients.size() - 1; i >= 0; --i) {
            if (!coefficients.get(i).equals(field.zero())) {
                size = i + 1;
                break;
            }
        }
        this.coefficients = this.coefficients.subList(0, size);
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
     * @return P(x) the evaluation result.
     */
    public T evaluate(T x) {
        T result = field.zero();
        for (int i = size - 1; i >= 0; --i) {
            result = field.add(coefficients.get(i), field.multiply(result, x));
        }
        return result;
    }

    /**
     * Returns the evaluation function for this polynomial.
     * 
     * @return The evaluation function for this polynomial.
     */
    public Function<T, T> getEvaluator() {
        return new Function<T, T>() {
            public T apply(T x) {
                return evaluate(x);
            }
        };
    }
    
    /**
     * Evaluates the polynomial at multiple points.
     * @param xs The points $x_i$ to evaluate
     * @return The array a defined by $a_i := P(x_i)$.
     */
    public ImmutableList<T> evaluateMulti(ImmutableList<T> xs) {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (T x : xs) {
            builder.add(evaluate(x));
        }
        return builder.build();
    }

    /**
     * Returns the coefficients list of this polynomial.
     * 
     * @return $c_i$
     */
    public ImmutableList<T> getCoefficients() {
        return coefficients;
    }

    /**
     * @return The size of the polynomial (degree plus one or 0 if the polynomial
     * is constant zero).
     */
    public int getSize() {
        return size;
    }
    
    /**
     * @return The degree of the polynomial or -1 if the polynomial 
     * is zero.
     */
    public int getDegree() {
        return size - 1;
    }

    /**
     * Generates a random polynomial over the field.
     * @param <T> The type of the field's elements over which the random polynomial is formed.
     * @param rnd The source of randomness
     * @param field An instance of the field over which the polynomial is formed.
     * @param size The size of the polynomial (see {@link getSize})
     * @return The generated polynomial
     */
    public static <T> Polynomial<T> createRandomPolynomial(Random rnd, Field<T> field, int size) {
        checkArgument(size >= 0, "Size must be >= 0, but is " + size);
        ImmutableList.Builder<T> coefficients = ImmutableList.builder();
        for (int i = 0; i < size - 1; ++i) {
            coefficients.add(field.randomElement(rnd));
        }
        T highest = field.zero();
        while (size > 0 && highest.equals(field.zero())) {
            highest = field.randomElement(rnd);
        }
        coefficients.add(highest);
        return new Polynomial<T>(field, coefficients.build());
    }
    
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        @SuppressWarnings("unchecked")
        Polynomial<T> other = (Polynomial<T>) other_;
        if (size != other.size) { return false; }
        for (int i = 0; i < size; ++i) {
            if (!coefficients.get(i).equals(other.coefficients.get(i))) {
                return false;
            }
        }
        return field.equals(other.field);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Polynomial over ");
        builder.append(field);
        builder.append(" (");
        for (int i = 0; i < size; ++i) {
            builder.append(coefficients.get(i));
            builder.append(' ');
        }
        builder.append(')');
        return builder.toString();
    }
}