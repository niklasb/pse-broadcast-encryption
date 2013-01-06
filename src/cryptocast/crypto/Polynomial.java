package cryptocast.crypto;

import java.util.List;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A polynomial $P$ over a field
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
    
    public static <T> Polynomial<T> zero(Field<T> field) {
        return new Polynomial<T>(field, ImmutableList.<T>of());
    }
    
    public static <T> Polynomial<T> one(Field<T> field) {
        return new Polynomial<T>(field, ImmutableList.of(field.one()));
    }
    
    public static <T> Polynomial<T> monomial(Field<T> field, T coeff, int exp) {
        Preconditions.checkArgument(exp >= 0, "Exponent must be >= 0");
        ImmutableList.Builder<T> coeffs = ImmutableList.builder();
        for (int i = 0; i < exp; ++i) {
            coeffs.add(field.zero());
        }
        coeffs.add(coeff);
        return new Polynomial<T>(field, coeffs.build());
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
        for (int i = size - 1; i >= 0; --i) {
            result = field.add(coefficients.get(i), field.multiply(result, x));
        }
        return result;
    }

    public Function<T, T> getEvaluator() {
        return new Function<T, T>() {
            public T apply(T x) {
                return evaluate(x);
            }
        };
    }
    
    public Polynomial<T> add(Polynomial<T> other) {
        assert field.equals(other.field);
        int newSize = size + other.size;
        ImmutableList.Builder<T> sumCoeffs = ImmutableList.builder();
        for (int i = 0; i < newSize; ++i) {
            T x = field.zero();
            if (i < size) {
                x = field.add(x, getCoefficient(i));
            }
            if (i < other.size) {
                x = field.add(x, other.getCoefficient(i));
            }
            sumCoeffs.add(x);
        }
        return new Polynomial<T>(field, sumCoeffs.build());
    }
    
    public Polynomial<T> multiply(T scalar) {
        ImmutableList.Builder<T> newCoeffs = ImmutableList.builder();
        for (T c : coefficients) {
            newCoeffs.add(field.multiply(c, scalar));
        }
        return new Polynomial<T>(field, newCoeffs.build());
    }

    public Polynomial<T> negate() {
        return multiply(field.negate(field.one()));
    }
    
    public Polynomial<T> subtract(Polynomial<T> other) {
        return add(other.negate());
    }
    
    private Polynomial<T> multiplyClassical(Polynomial<T> other) {
        int newSize = size + other.size;
        List<T> prodCoeffs = Lists.newArrayListWithCapacity(newSize);
        for (int i = 0; i < newSize; ++i) {
            prodCoeffs.add(field.zero());
        }
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < other.size; ++j) {
                int k = i + j;
                T x = field.multiply(coefficients.get(i), other.coefficients.get(j));
                prodCoeffs.set(k, field.add(prodCoeffs.get(k), x));
            }
        }
        return new Polynomial<T>(field, prodCoeffs);
    }
    
    public Polynomial<T> multiply(Polynomial<T> other) {
        assert field.equals(other.field);
        if (field instanceof IntegersModuloPrime) {
            BigInteger mod = ((IntegersModuloPrime) field).getP();
            return (Polynomial<T>)new Polynomial<BigInteger>((Field<BigInteger>)field, FastPolynomialMultiplication.multiply(
                    (List<BigInteger>)coefficients, 
                    (List<BigInteger>)other.coefficients, mod));
        }
        return multiplyClassical(other);
    }
    
    public Polynomial<T> reverse() {
        return new Polynomial<T>(field, coefficients.reverse());
    }
    
    public Polynomial<T> reverse(int k) {
        Preconditions.checkArgument(k >= getDegree());
        ImmutableList.Builder<T> coeffs = ImmutableList.builder();
        for (int i = k - getDegree(); i > 0; --i) {
            coeffs.add(field.zero());
        }
        return new Polynomial<T>(field, coeffs.addAll(coefficients.reverse()).build());
    }
    
    /**
     * @param k The exponent of x
     * @return $P \mod x^k$
     */
    public Polynomial<T> modPowerOfX(int k) {
        if (k >= size) {
            return this;
        } else {
            return new Polynomial<T>(field, coefficients.subList(0, k));
        }
    }
    
    public Polynomial<T> square() {
        return multiply(this);
    }
    
    /**
     * Precondition: size > 0 and coefficients[0] != 0
     * @param k must be non-negative
     * @return Polynomial $U$ with $1 - PU \equiv 0 \mod{x^k}$
     */
    public Polynomial<T> inversePolyModPowerOfX(int k) {
        assert k >= 0;
        assert size > 0;
        T firstCoeff = getCoefficient(0),
          firstCoeffInv = field.invert(firstCoeff);
        assert !firstCoeff.equals(field.zero());
        Polynomial<T> g = multiply(firstCoeffInv);
        Polynomial<T> h = one(field);
        for (int i = 0;; ++i) {
            int e = 1 << i;
            h = h.multiply(field.two()).subtract(
                    g.multiply(h.square())).modPowerOfX(e);
            if (k < e) { break; }
        }
        return h.modPowerOfX(k).multiply(firstCoeffInv);
    }
    
    public static class DivMod<T> {
        public Polynomial<T> div, mod;
        public DivMod(Polynomial<T> div, Polynomial<T> mod) {
            this.div = div;
            this.mod = mod;
        }
    }
    
    public DivMod<T> divMod(Polynomial<T> other) {
        assert other.getSize() > 0;
        assert field.equals(other.field);
        if (other.getDegree() > getDegree()) {
            return new DivMod<T>(Polynomial.zero(field), this);
        }
        int n = getDegree(), m = other.getDegree();
        Polynomial<T> brev = other.reverse().inversePolyModPowerOfX(n - m + 1),
                      q = reverse().multiply(brev).modPowerOfX(n - m + 1).reverse(n - m),
                      r = subtract(q.multiply(other));
        return new DivMod<T>(q, r);
    }
    
    public Polynomial<T> div(Polynomial<T> other) {
        return divMod(other).div;
    }
    
    public Polynomial<T> mod(Polynomial<T> other) {
        return divMod(other).mod;
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
     * @param i The index of the coefficient to get ($0 \leq i \leq n$), where
     *          $n$ is the degree of the polynomial.
     * @return $c_i$
     */
    public T getCoefficient(int i) {
        return coefficients.get(i);
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
            if (!getCoefficient(i).equals(other.getCoefficient(i))) {
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