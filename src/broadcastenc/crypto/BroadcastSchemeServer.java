package broadcastenc.crypto;

import java.util.Collection;

class Identity {
}

public interface BroadcastSchemeServer<T, U> {
  public void revoke(Identity i);
  public Collection<? extends Share<T, U>> getShares();
}
