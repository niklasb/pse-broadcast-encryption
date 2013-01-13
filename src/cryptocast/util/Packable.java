package cryptocast.util;

import java.nio.ByteBuffer;

/**
 * Defines functionality for a packable class.
 */
public interface Packable {
	
    /**
     * Returns the maximum space.
     * 
     * @return The maximum space.
     */
    public int getMaxSpace();
    /**
     * Used to pack a byte buffer.
     * 
     * @param buf A byte buffer.
     */
    public void pack(ByteBuffer buf);
}
