package cryptocast.crypto;

import java.io.Serializable;
import java.util.Set;

import cryptocast.util.CanBeObserved;

/**
 * Manages a set of user identities.
 * The state of this class is the set of revoked users.
 * When it changes, the observers are informed about a change.
 * 
 * @param <ID> The type of the identities
 */
public interface BroadcastSchemeUserManager<ID> extends CanBeObserved, Serializable {
    /**
     * @param i An index
     * @return The identity with the given index
     */
    public abstract ID getIdentity(int i);
    
    /**
     * Revokes a user.
     * The observers will be informed if the set of revoked
     * users has changed.
     * 
     * @param ids The identities of the user.
     * @return true, if the set of revoked users changed or false otherwise
     */
    public boolean revoke(ID id) throws NoMoreRevocationsPossibleError;
    
    /**
     * Revokes multiple users. Either all or no user is revoked.
     * The observers will be informed if the set of revoked
     * users has changed.
     * 
     * @param ids The identities of the user.
     * @return true, if the set of revoked users changed or false otherwise
     */
    public boolean revoke(Set<ID> ids) throws NoMoreRevocationsPossibleError;
    
    /** Unrevokes a user and informs the observers if the set
     * of revoked users has changed.
     * 
     * @param id The user identity to revoke
     * @return true, if the set of revoked users changed or false otherwise
     */
    public boolean unrevoke(ID id);
    
    /**
     * @param id The identity of the user
     * @return Whether the user is revoked.
     */
    public abstract boolean isRevoked(ID id);
}