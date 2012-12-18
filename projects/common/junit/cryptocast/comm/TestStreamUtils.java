package cryptocast.comm;

import static org.junit.Assert.*;

import java.io.InputStream;
import static cryptocast.comm.TestUtils.*;

import org.junit.Test;

public class TestStreamUtils {
    @Test
    public void recvallWorks() throws Exception {
        byte[] expected = str2bytes("abcdefghijk");
        InputStream in = new MemoryInputStream(expected, 2);
        int size = expected.length;
        byte[] actual = new byte[size];
        StreamUtils.readall(in, actual, 0, size);
        assertArrayEquals(expected, actual);
    }
}
