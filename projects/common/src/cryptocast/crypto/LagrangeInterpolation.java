package cryptocast.crypto;

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
        @SuppressWarnings("unchecked")
        T[] lambdas = (T[])new Object[xs.length];
        int len = xs.length;
        for (int i = 0; i < len; ++i) {
            T res = field.one();
            for (int j = 0; j < len; ++j) {
                if (i == j) { continue; }
                res = field.multiply(res, 
                          field.divide(xs[i], 
                              field.subtract(xs[i], xs[j])));
            }
        }
        return lambdas;
    }
}
