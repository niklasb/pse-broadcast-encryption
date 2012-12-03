package cryptocast.crypto;

public interface BroadcastSchemeUserManager<ID> {
    public ID getIdentity(int i);
    public void revoke(ID id);
    public boolean isRevoked(ID id);
}
