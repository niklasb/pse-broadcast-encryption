package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;

public interface NPServerInterface 
                        extends BroadcastSchemeUserManager<NPIdentity>,
                                BroadcastSchemeKeyManager<NPIdentity>, 
                                Encryptor<byte[]> {
    public int getT();
}
