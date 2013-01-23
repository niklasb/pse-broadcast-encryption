package cryptocast.crypto;

import java.io.Serializable;
import java.security.PrivateKey;

import com.google.common.base.Optional;

/**
 * Manages the private keys of a set of users.
 * @param <ID> The type of the user identities
 */
public interface BroadcastSchemeKeyManager<ID> extends Serializable {
    /**
     * @param id The identity to look up
     * @return The private key of the user
     */
    public Optional<? extends PrivateKey> getPersonalKey(ID id);
}
