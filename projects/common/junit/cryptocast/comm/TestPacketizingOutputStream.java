package cryptocast.comm;

import static org.junit.Assert.*;
import static cryptocast.comm.TestUtils.*;

import org.junit.Test;

public class TestPacketizingOutputStream {
    @Test
    public void writeWorks() throws Exception {
        byte[] buffer = new byte[4096];
        MemoryInputStream pipeIn = new MemoryInputStream(buffer, 2);
        MemoryOutputStream pipeOut = new MemoryOutputStream(buffer);
        MessageInChannel in = new MessageInChannel(pipeIn);
        MessageOutChannel out = new MessageOutChannel(pipeOut);
        PacketizingOutputStream sut = new PacketizingOutputStream(out, 2);
        sut.write(str2bytes("aabbccdd"));
        assertArrayEquals(str2bytes("aa"), in.recvMessage());
        assertArrayEquals(str2bytes("bb"), in.recvMessage());
        assertArrayEquals(str2bytes("cc"), in.recvMessage());
        assertArrayEquals(str2bytes("dd"), in.recvMessage());
    }
}