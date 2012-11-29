package broadcastenc.crypto;

class InsufficientInformationException extends Exception {}

/**
 * @param U phantom type to ensure that two Share<T, U> are always compatible
 */
public interface Share<T, U> {
  public Share<T, U> update(Share<T, U> other);
  public T restore() throws InsufficientInformationException;
  public byte[] pack();
}

