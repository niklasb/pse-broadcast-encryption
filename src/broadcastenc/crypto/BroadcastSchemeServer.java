package broadcastenc.crypto;

import java.util.Collection;

class Identity { }

public interface BroadcastSchemeServer<T, U extends Share<T>> {
    public void revoke(Identity i);
    public Collection<U> getShares();
}
