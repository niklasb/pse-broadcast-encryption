package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.util.Collection;

public class NaorPinkasServer
      implements BroadcastSchemeUserManager<NaorPinkasIdentity>,
                 BroadcastSchemeKeyManager<NaorPinkasIdentity>,
                 Serializable,
                 Encryptor<BigInteger> {
    public ByteBuffer encrypt(BigInteger secret) {return null;}
    public NaorPinkasIdentity getIdentity(int i) {return null;}
    public void revoke(NaorPinkasIdentity x) {}
    public boolean isRevoked(NaorPinkasIdentity id) {return false;}
    public NaorPinkasPersonalKey getPersonalKey(NaorPinkasIdentity id) {return null;}
}
