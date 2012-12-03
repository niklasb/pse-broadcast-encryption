package cryptocast.crypto;

import java.nio.ByteBuffer;

public interface BroadcastSchemeServerContext<T, ID> {
    public ID getIdentity(int i);
    public void revoke(ID id);
    public boolean isRevoked(ID id);
}

public class BroadcastSchemeServerEncryption<T, ID> {
    private BroadcastSchemeServerEncryption<T, ID> context;

    public ByteBuffer encrypt(T secret);
}
