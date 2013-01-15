package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;

public interface NaorPinkasServerInterface 
                        extends BroadcastSchemeUserManager<NaorPinkasIdentity>,
                                BroadcastSchemeKeyManager<NaorPinkasIdentity>, 
                                Encryptor<byte[]> {
    public int getT();
}
