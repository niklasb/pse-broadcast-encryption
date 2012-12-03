package broadcastenc.crypto;

import java.nio.ByteBuffer;

public interface BroadcastSchemeServerContext<T, ID> {
    public void revoke(ID id);
    public ID getIdentity(int i);
    public ByteBuffer encrypt(T secret);
}
