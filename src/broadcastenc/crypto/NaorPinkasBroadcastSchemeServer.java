package broadcastenc.crypto;

import java.math.BigInteger;
import java.util.Collection;

public class NaorPinkasBroadcastSchemeServer
      implements BroadcastSchemeServer<BigInteger,
                                       NaorPinkasShare,
                                       NaorPinkasBroadcastSchemeServer.Identity> {
    class Identity {
      private Identity(BigInteger id) {}
      public BigInteger getId() { return null; }
    }

    public void revoke(Identity x) { }
    public Collection<NaorPinkasShare> getShares() { return null; }
}