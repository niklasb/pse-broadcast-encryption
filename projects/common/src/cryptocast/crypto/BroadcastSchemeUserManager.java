package cryptocast.crypto;

/**
 * Manages a set of user identities.
 * @param <ID> The type of the identities
 */
public interface BroadcastSchemeUserManager<ID> {
    /**
     * @param i An index
     * @return The identity with the given index
     */
    public ID getIdentity(int i);
    
    /**
     * Revokes a user.
     * @param id The identity of the user
     * @return true, if the set of revoked users changed or false otherwise
     */
    public boolean revoke(ID id) throws NoMoreRevocationsPossibleError;
    
    /**
     * Authorizes a user.
     * @param id The identity of the user
     * @return true, if the set of revoked users changed or false otherwise
     */
    public boolean unrevoke(ID id);
    
    /**
     * @param id The identity of the user
     * @return Whether the user is revoked.
     */
    public boolean isRevoked(ID id);
}