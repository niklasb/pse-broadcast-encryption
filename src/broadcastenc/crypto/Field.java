package broadcastenc.crypto;

import java.lang.ArithmeticException;

/**
 * @param T The values we work on
 */
public abstract class Field<T> {
  public abstract T add(T a, T b);
  public abstract T multiply(T a, T b);
  public abstract T negate(T a);
  public abstract T invert(T a) throws ArithmeticException;
  public abstract T zero();
  public abstract T one();
  public abstract T randomElement();

  public T subtract(T a, T b) {
    return add(a, negate(b));
  }
  public T divide(T a, T b) {
    return multiply(a, invert(b));
  }
  public T pow(T a, int e) {
    T result = one();
    for (int i = 0; i < e; ++i)
      result = multiply(result, a);
    return result;
  }
}
