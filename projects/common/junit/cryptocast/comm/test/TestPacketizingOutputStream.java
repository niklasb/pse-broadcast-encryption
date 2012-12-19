package cryptocast.comm.test;

import static org.junit.Assert.*;
import static cryptocast.comm.TestUtils.*;

import org.junit.Test;

public class TestPacketizingOutputStream {
    @Test
    public void writeWorks() throws Exception {
        byte[] buffer = new byte[4096];
        MemoryInputStream pipeIn = new MemoryInputStream(buffer, 2);
        MemoryOutputStream pipeOut = new MemoryOutputStream(buffer);
        RawMessageInChannel in = new RawMessageInChannel(pipeIn);
        RawMessageOutChannel out = new RawMessageOutChannel(pipeOut);
        PacketizingOutputStream sut = new PacketizingOutputStream(out, 2);
        sut.write("aabbccdd".getBytes());
        assertArrayEquals(str2bytes("aa"), in.recvMessage());
        assertArrayEquals(str2bytes("bb"), in.recvMessage());
        assertArrayEquals(str2bytes("cc"), in.recvMessage());
        assertArrayEquals(str2bytes("dd"), in.recvMessage());
    }
}
