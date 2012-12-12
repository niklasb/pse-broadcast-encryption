package cryptocast.crypto;

import java.util.Collection;
import com.google.common.base.Optional;

/**
 * Implements a strategy to restore a secret from a number of shares.
 * @param <S> The type of the secret
 * @param <T> The type of the shares
 */
public interface ShareCombinator<S, T> {
  /**
   * Restores a secret from several shares.
   * @param shares The shares
   * @return The reconstructed secret or absent if the information represented
   * by the given shares is insufficient to restore it.
   */
  public Optional<S> restore(Collection<T> shares);
}
