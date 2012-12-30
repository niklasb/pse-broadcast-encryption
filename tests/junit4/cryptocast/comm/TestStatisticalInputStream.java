package cryptocast.comm;

import static org.junit.Assert.*;
import static cryptocast.util.ByteUtils.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

public class TestStatisticalInputStream {

    @Test
    public void works() throws Exception {
        InputStream inner = new ByteArrayInputStream(str2bytes("abcdef"));
        StatisticalInputStream sut = new StatisticalInputStream(inner);
        int count = 0;
        assertEquals(str2bytes("a")[0], sut.read());
        count++;
        byte[] buffer = new byte[4];
        count += sut.read(buffer);
        assertArrayEquals(str2bytes("bcde"), buffer);
        count += sut.read(buffer, 1, 3);
        assertEquals(count, 6);
        assertEquals(str2bytes("f")[0], buffer[1]);
    }

}
