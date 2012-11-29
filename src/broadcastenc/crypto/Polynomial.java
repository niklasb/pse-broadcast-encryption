package broadcastenc.crypto;

public class Polynomial<T, U extends Field<T>> {
  public Polynomial(T[] coefficients) { }

  public T evaluate(T x) { return null; }
  public T[] evaluateMulti(T[] x) { return null; }
  public T getCoefficient(int i) { return null; }
  public static <T, U extends Field<T>> Polynomial<T, U>
    createRandomPolynomial(int degree) { return null; }
}
