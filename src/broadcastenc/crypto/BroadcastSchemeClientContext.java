package broadcastenc.crypto;

import java.nio.ByteBuffer;

public interface BroadcastSchemeClientContext<T> {
    public T decrypt(ByteBuffer message);
}
