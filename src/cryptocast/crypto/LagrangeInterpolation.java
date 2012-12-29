package cryptocast.crypto;

import java.lang.reflect.Array;

/**
 * Performs a Lagrange interpolation of a polynomial.
 * @param <T> The type of items of the polynomial over a field.
 */
public class LagrangeInterpolation {
    /**
     * @return The Lagrange coefficients $\lambda_i$ of the associated polynomial,
     *         where $\lambda_i = prod_{j \neq i} \frac{x_i}{x_i - x_j}$
     */
    public static <T> T[] computeCoefficients(Field<T> field, T[] xs) {
        int len = xs.length;
        @SuppressWarnings("unchecked")
        T[] lambdas = (T[])Array.newInstance(field.getElementClass(), len);
        for (int i = 0; i < len; ++i) {
            T res = field.one();
            for (int j = 0; j < len; ++j) {
                if (i == j) { continue; }
                res = field.multiply(res,
                          field.divide(xs[j],
                              field.subtract(xs[j], xs[i])));
            }
            lambdas[i] = res;
        }
        return lambdas;
    }
}
