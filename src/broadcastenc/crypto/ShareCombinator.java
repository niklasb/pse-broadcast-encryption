package broadcastenc.crypto;

/** Implements a strategy to merge multiple Shares into a single share with
 * more information.
 */
public interface ShareCombinator<T, U extends Share<T>> {
  /**
   * Combines two shares.
   * @param a The first share
   * @param b The second share
   * @return a new share containing the information from both a and b.
   */
  public U combine(U a, U b);
}
