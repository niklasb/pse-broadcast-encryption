package cryptocast.comm;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import static cryptocast.comm.TestUtils.*;

public class TestMessageInChannel {
    @Test
    public void test() throws Exception {
        byte[] packed = str2bytes(
                "\u0000\u0000\u0000\u0003abc\u0000\u0000\u0000\u0005defgh");
        InputStream in = new MemoryInputStream(packed, 5);
        MessageInChannel msg = new MessageInChannel(in);
        assertArrayEquals(str2bytes("abc"), msg.recvMessage());
        assertArrayEquals(str2bytes("defgh"), msg.recvMessage());
    }
}
