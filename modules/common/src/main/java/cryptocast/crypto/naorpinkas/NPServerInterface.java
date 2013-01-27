package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;

/** The common interface of NP servers based on different underlying group
 * structures. */
public interface NPServerInterface
                        extends BroadcastSchemeUserManager<NPIdentity>,
                                BroadcastSchemeKeyManager<NPIdentity>,
                                Encryptor<byte[]> {
    /** @return $t$ */
    public int getT();
}
