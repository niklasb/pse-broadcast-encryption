package cryptocast.comm.test;

import static org.junit.Assert.*;

import java.io.InputStream;
import static cryptocast.util.ByteStringUtils.*;
import cryptocast.comm.*;

import org.junit.Test;

public class TestStreamUtils {
    @Test
    public void recvallWorks() throws Exception {
        byte[] expected = str2bytes("abcdefghijk");
        InputStream in = new MemoryInputStream(expected, 2);
        int size = expected.length;
        byte[] actual = new byte[size];
        assertEquals(size, StreamUtils.readall(in, actual, 0, size));
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void recvallWorksOnEOF() throws Exception {
        byte[] expected = str2bytes("abc");
        InputStream in = new MemoryInputStream(expected, 2);
        byte[] actual = new byte[4];
        assertEquals(3, StreamUtils.readall(in, actual, 0, 4));
        assertEquals(0, StreamUtils.readall(in, actual, 0, 4));
    }
}
