package cryptocast.crypto;

/**
 * Manages a set of user identites
 * @param <ID> The type of the identities
 */
public interface BroadcastSchemeUserManager<ID> {
    /**
     * @param i An index
     * @return The identity with the given index
     */
    public ID getIdentity(int i);
    /**
     * Revokes a user
     * @param id The identity of the user
     */
    public void revoke(ID id);
    /**
     * @param id The identity of the user
     * @return whether the user is revoked
     */
    public boolean isRevoked(ID id);
}
