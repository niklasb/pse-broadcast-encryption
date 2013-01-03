package cryptocast.crypto;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cryptocast.util.MapUtils;

/**
 * Performs a Lagrange interpolation of a polynomial.
 * @param <T> The type of items of the polynomial over a field.
 */
public class LagrangeInterpolation<T> {
    private Map<T, T> coefficients;
    private Field<T> field;
    
    public Map<T, T> getCoefficients() {
        return coefficients;
    }
    
    public Field<T> getField() {
        return field;
    }
    
    public LagrangeInterpolation(Field<T> field) {
        this.field = field;
        this.coefficients = Maps.newHashMap();
    }
    
    public LagrangeInterpolation(Field<T> field, Map<T, T> coefficients) {
        this.field = field;
        this.coefficients = Maps.newHashMap(coefficients);
    }
    
    public static <T> LagrangeInterpolation<T> fromXs(Field<T> field, ImmutableList<T> xs) {
        ImmutableList<T> coeffs = computeCoefficients(field, xs);
        return new LagrangeInterpolation<T>(field, MapUtils.zip(xs, coeffs));
    }
    
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
    
    public T interpolateP0(final Map<T, T> dataPoints) {
        setXs(dataPoints.keySet());
        return interpolateP0(new Function<T, T>() {
            public T apply(T x) {
                return dataPoints.get(x);
            }
        });
    }
    
    public void setXs(Set<T> xs) {
        Sets.SetView<T> removeXs = Sets.difference(coefficients.keySet(), xs);
        Set<T> newXs = Sets.difference(xs, coefficients.keySet());
        removeXs(removeXs.immutableCopy());
        addXs(newXs);
    }
    
    public void addXs(Set<T> newXs) {
        for (T x : newXs) {
            addX(x);
        }
    }
    
    public void removeXs(Set<T> removeXs) {
        for (T x : removeXs) {
            removeX(x);
        }
    }
    
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
     * @return The Lagrange coefficients $\c_i$ of the associated polynomial,
     *         where $\c_i = prod_{j \neq i} \frac{x_j}{x_j - x_i}$
     */
    public static <T> ImmutableList<T> computeCoefficients(
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
    
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        @SuppressWarnings("unchecked")
        LagrangeInterpolation<T> other = (LagrangeInterpolation<T>) other_;
        return field.equals(other.field)
            && coefficients.equals(other.coefficients);
    }
}