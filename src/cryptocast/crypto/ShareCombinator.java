package cryptocast.crypto;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Implements a strategy to restore a secret from a number of shares.
 * @param <S> The type of the secret
 * @param <T> The type of the shares
 */
public interface ShareCombinator<S, T, A> {
  /**
   * Restores a secret from several shares.
   * @param shares The shares
   * @param additionalInfo some additional, public information that
   *                       helps restore the secret (possibly precomputed data)
   * @return The reconstructed secret or absent if the information represented
   * by the given shares is insufficient to restore it.
   */
  public Optional<S> restore(ImmutableList<T> shares, A additionalInfo);
}
