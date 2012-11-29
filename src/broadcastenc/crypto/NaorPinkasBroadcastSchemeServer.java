package broadcastenc.crypto;

import java.math.BigInteger;
import java.util.Collection;

public class NaorPinkasBroadcastSchemeServer
        implements BroadcastSchemeServer<BigInteger, NaorPinkasShare>
{
    public void revoke(Identity x) { }
    public Collection<NaorPinkasShare> getShares() {
        return null;
    }
}
