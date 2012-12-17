package cryptocast.crypto;

import java.security.PrivateKey;

/**
 * Manages the private keys of a set of users.
 * @param <ID> The type of the user identities
 */
public interface BroadcastSchemeKeyManager<ID> {
    /**
     * @param id The identity to look up
     * @return The private key of the user
     */
    public PrivateKey getPersonalKey(ID id);
}
