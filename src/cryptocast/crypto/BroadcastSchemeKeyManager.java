package cryptocast.crypto;

import java.security.PrivateKey;

public interface BroadcastSchemeKeyManager<ID> {
    public PrivateKey getPersonalKey(ID id);
}
