package broadcastenc.crypto;

/**
 * A polynomial $P$ over a field
 * @param T The type of the field's elements
 * @param U The field implementation
 */
public class Polynomial<T, U extends Field<T>> {
    /**
     * Initializes a polynomial
     * @param coefficients The coefficients $c_i$ of the polynomial ($0 <= i <= n$).
     * The polynomial is defined as $P(x) := sum_{i=0}^n c_i x^i = c_0 + c_1
     * x + ... + c_n x^n$
     */
    public Polynomial(T[] coefficients) { }

    /**
     * Evaluates the polynomial at a single point.
     * @param x The point
     * @return P(x)
     */
    public T evaluate(T x) { return null; }
    /**
     * Evaluates the polynomial at multiple points in $O\left(n \cdot \log
     * n\right)$
     * @param xs The points $x_i$ to evaluate
     * @return The array a defined by $a_i := P(x_i)$
     */
    public T[] evaluateMulti(T[] xs) { return null; }
    /**
     * @param The coefficient to get
     * @return $c_i$
     */
    public T getCoefficient(int i) { return null; }
    /**
     * Generates a random polynomial over the field
     * @param degree The degree of the generated polynomial
     * @return The generated polynomial
     */
    public static <T, U extends Field<T>> Polynomial<T, U>
      createRandomPolynomial(int degree) { return null; }
}
