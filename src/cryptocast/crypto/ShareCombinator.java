package cryptocast.crypto;

/**
 * Implements a strategy to merge multiple Shares into a single share with
 * more information.
 * @param <S> The type of the secret
 * @param <T> The type of the shares
 */
public interface ShareCombinator<S, T extends Share<S>> {
  /**
   * Combines two shares.
   * @param a The first share
   * @param b The second share
   * @return a new share containing the information from both a and b.
   */
  public T combine(T a, T b);
}
