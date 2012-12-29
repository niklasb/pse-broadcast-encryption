package cryptocast.util;

import java.nio.ByteBuffer;

public interface Packable {
    public int getMaxSpace();
    public void pack(ByteBuffer buf);
}
