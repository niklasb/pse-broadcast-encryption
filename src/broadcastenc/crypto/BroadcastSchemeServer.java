package broadcastenc.crypto;

import java.util.Collection;

/** The server side of a broadcast encryption scheme.
 * @param T The type of the secret
 * @param U The type of the shares
 * @param V The type of the identities
 */
public interface BroadcastSchemeServer<T, U extends Share<T>, V> {
    public void revoke(V i);
    public Collection<U> getShares();
}
