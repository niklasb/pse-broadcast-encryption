package cryptocast.comm;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import static cryptocast.comm.BufferBackedChannel.str2bytes;

import org.junit.Test;

public class TestInChannel {
    @Test
    public void recvallWorks() throws Exception {
        byte[] expected = str2bytes("abcdefgh");
        InChannel chan = BufferBackedChannel.createInChannel(expected, 2);
        int size = expected.length;
        System.out.println(size);
        byte[] actual = new byte[size];
        chan.recvall(ByteBuffer.wrap(actual));
        assertArrayEquals(expected, actual);
    }
}
