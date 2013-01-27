package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cryptocast.util.MapUtils;
import cryptocast.util.NativeUtils;

/**
 * Represents the context needed to quickly perform a Lagrange interpolation of
 * the values $P_i(0)$ of arbitrarily many polnomials $P_i$.
 * Given a set of points ${x_1, ..., x_n}$, the state needed to do this is the
 * values $c_i = \prod_{i \neq j} \frac{x_j}{x_j - x_i}$.
 * @param <T> The type of items of the polynomial over a field.
 */
public class LagrangeInterpolation<T> implements Serializable {
    private static int DEFAULT_NUM_THREADS = 2;

    private static final long serialVersionUID = 4096286734152138209L;
    private static final Logger log = LoggerFactory
            .getLogger(LagrangeInterpolation.class);

    private static boolean haveNative =
            NativeUtils.tryToLoadNativeLibOrLogFailure("LagrangeInterpolation", log);

    private Map<T, T> coefficients;
    private Field<T> field;

    /**
     * @return The Lagrange coefficients. This is a map $T \to T$ that assigns places $x_i$
     * their coefficients
     * $c_i = \prod_{i \neq j} \frac{x_j}{x_j - x_i}$.
     */
    public Map<T, T> getCoefficients() {
        return coefficients;
    }

    /**
     * @return The field of the interpolation.
     */
    public Field<T> getField() {
        return field;
    }

    /**
     * Creates an instance of this class.
     *
     * @param field The field over which the interpolation is constructed.
     */
    public LagrangeInterpolation(Field<T> field) {
        this.field = field;
        this.coefficients = Maps.newHashMap();
    }

    /**
     * Creates an instance of this class.
     *
     * @param field The field over which the interpolation is constructed.
     * @param coefficients The initial lagrange coefficients, which is a map
     * $T \to T$ that assigns places $x_i$ their coefficients
     * $c_i = \prod_{i \neq j} \frac{x_j}{x_j - x_i}$.
     */
    public LagrangeInterpolation(Field<T> field, Map<T, T> coefficients) {
        this.field = field;
        this.coefficients = Maps.newHashMap(coefficients);
    }

    /**
     * Creates an instance of this class from a list of points.
     *
     * @param field The field over which the interpolation is constructed.
     * @param xs The list of points $x_i$.
     * @param numThreads The number of threads for concurrent evaluation.
     * @return An instance of this class from a list of points.
     */
    public static <T> LagrangeInterpolation<T> fromXs(Field<T> field, ImmutableList<T> xs,
                                                      int numThreads) {
        ImmutableList<T> coeffs = computeCoefficients(field, xs, numThreads);
        return new LagrangeInterpolation<T>(field, MapUtils.zip(xs, coeffs));
    }

    /**
     * Creates an instance of this class from a list of points using a default number of threads.
     *
     * @param field The field over which the polynomial is constructed.
     * @param xs The list of points.
     * @return An instance of this class from a list of points using default number of threads.
     */
    public static <T> LagrangeInterpolation<T> fromXs(Field<T> field, ImmutableList<T> xs) {
        return fromXs(field, xs, DEFAULT_NUM_THREADS);
    }

    /**
     * Interpolates $P(0)$ using the function $P$ to compute the values
     * $P(x_i)$.
     *
     * @param evaluate The function that evaluates the polynomial $P$ at the
     * given points.
     * @return The interpolation result.
     */
    public T interpolateP0(Function<? super T, T> evaluate) {
        T sum = field.zero();
        for (Map.Entry<T, T> entry : coefficients.entrySet()) {
            T x = entry.getKey();
            T c = entry.getValue();
            T y = evaluate.apply(x);
            assert y != null;
            sum = field.add(sum,  field.multiply(y, c));
        }
        return sum;
    }

    /**
     * Interpolates $P(0)$ where $P$ is the Lagrange polynomial of
     * the given data point.
     *
     * @param dataPoints The data points.
     * @return The interpolation result.
     */
    public T interpolateP0(final Map<T, T> dataPoints) {
        setXs(dataPoints.keySet());
        return interpolateP0(new Function<T, T>() {
            public T apply(T x) {
                return dataPoints.get(x);
            }
        });
    }

    /**
     * Sets the set ${x_1, ..., x_n}$.
     * @param xs The set of points.
     */
    public void setXs(Set<T> xs) {
        Sets.SetView<T> removeXs = Sets.difference(coefficients.keySet(), xs);
        Set<T> newXs = Sets.difference(xs, coefficients.keySet());
        removeXs(removeXs.immutableCopy());
        addXs(newXs);
    }

    /**
     * Adds the set ${x_{n+1}, ..., x_{n+m}}$ to the interal set of points.
     * @param newXs The new points.
     */
    public void addXs(Set<T> newXs) {
        for (T x : newXs) {
            addX(x);
        }
    }

    /**
     * Removes the set ${x_{n-m}, ..., x_n}$ from the internal set of points.
     * @param removeXs The points to remove
     */
    public void removeXs(Set<T> removeXs) {
        for (T x : removeXs) {
            removeX(x);
        }
    }

    /**
     * Adds a point to the internal set of points
     * @param newX The point to add.
     */
    public void addX(T newX) {
        if (coefficients.containsKey(newX)) {
            return;
        }

        T newC = field.one();
        for (Map.Entry<T, T> entry : coefficients.entrySet()) {
            T x = entry.getKey();
            T c = entry.getValue();
            c = field.multiply(c, field.divide(newX, field.subtract(newX, x)));
            entry.setValue(c);
            newC = field.multiply(newC, field.subtract(x, newX));
        }
        newC = field.invert(newC);
        for (T x : coefficients.keySet()) {
            newC = field.multiply(newC, x);
        }

        coefficients.put(newX, newC);
    }

    /**
     * Removes a point from the internal set of points.
     * @param removedX The point to remove.
     */
    public void removeX(T removedX) {
        if (!coefficients.containsKey(removedX)) {
            return;
        }
        coefficients.remove(removedX);

        for (Map.Entry<T, T> entry : coefficients.entrySet()) {
            T x = entry.getKey();
            T c = entry.getValue();
            c = field.multiply(c, field.divide(field.subtract(removedX, x), removedX));
            entry.setValue(c);
        }
    }

    /**
     * Computes the langrange coefficients for a given list of points.
     * Uses native acceleration if available.
     *
     * @param field The field
     * @param xs The list $x_i$
     * @param numThreads the number of threds to use for parallelisation
     * @return The Lagrange coefficients $c_i$,
     *         where $c_i = \prod_{j \neq i} \frac{x_j}{x_j - x_i}$
     */
    @SuppressWarnings("unchecked")
    public static <T> ImmutableList<T> computeCoefficients(
            Field<T> field, ImmutableList<T> xs, int numThreads) {
        if (haveNative && field instanceof IntegersModuloPrime) {
            BigInteger mod = ((IntegersModuloPrime) field).getP();
            byte[][] xsTwoComplements = NativeUtils.bigIntListToTwoComplements(
                    (ImmutableList<BigInteger>) xs);
            byte[][] result = nativeComputeCoefficients(
                    xsTwoComplements, mod.toByteArray(), numThreads);
            return (ImmutableList<T>) NativeUtils.twoComplementsToBigIntList(result);
        }
        return genericComputeCoefficients(field, xs);
    }

    private static <T> ImmutableList<T> genericComputeCoefficients(
            Field<T> field, ImmutableList<T> xs) {
        T z = field.one();
        for (T x : xs) {
            z = field.multiply(z, x);
        }
        ImmutableList.Builder<T> cs = ImmutableList.builder();
        int len = xs.size();
        for (int i = 0; i < len; ++i) {
            T prod = xs.get(i);
            for (int j = 0; j < len; ++j) {
                if (i == j) { continue; }
                prod = field.multiply(prod, field.subtract(xs.get(j), xs.get(i)));
            }
            cs.add(field.divide(z, prod));
        }
        return cs.build();
    }

    private static native byte[][] nativeComputeCoefficients(byte[][] xs, byte[] mod, int numThreads);

    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        @SuppressWarnings("unchecked")
        LagrangeInterpolation<T> other = (LagrangeInterpolation<T>) other_;
        return field.equals(other.field)
            && coefficients.equals(other.coefficients);
    }
}
