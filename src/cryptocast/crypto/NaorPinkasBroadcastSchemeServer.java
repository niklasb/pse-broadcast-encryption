package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;

public class NaorPinkasBroadcastSchemeServer
      implements BroadcastSchemeServer<BigInteger,
                                       NaorPinkasShare,
                                       NaorPinkasBroadcastSchemeServer.Identity>,
                 Serializable {
    class Identity {
      private Identity(BigInteger id) {}
      private BigInteger getId() { return null; }
    }

    public void revoke(Identity x) { }
    public Collection<NaorPinkasShare> getShares() { return null; }
}
