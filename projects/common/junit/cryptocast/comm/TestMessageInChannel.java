package cryptocast.comm;

import static cryptocast.comm.BufferBackedChannel.str2bytes;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestMessageInChannel {
    @Test
    public void test() throws Exception {
        byte[] packed = str2bytes(
                "\u0000\u0000\u0000\u0003abc\u0000\u0000\u0000\u0005defgh");
        InChannel chan = BufferBackedChannel.createInChannel(packed, 5);
        MessageInChannel msg = new MessageInChannel(chan);
        assertArrayEquals(str2bytes("abc"), msg.recvMessage());
        assertArrayEquals(str2bytes("defgh"), msg.recvMessage());
    }
}
