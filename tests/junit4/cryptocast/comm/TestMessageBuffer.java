package cryptocast.comm;

import static org.junit.Assert.*;

import org.junit.Test;

import cryptocast.comm.MessageBuffer;
import static cryptocast.util.ByteUtils.str2bytes;

public class TestMessageBuffer {

    @Test
    public void sendRecvWorks() throws Exception {
        MessageBuffer sut = new MessageBuffer();
        byte[][] messages = { 
            str2bytes("abc"),
            str2bytes("def"),
            str2bytes("ghi"),
        };
        for (byte[] msg : messages) {
            sut.sendMessage(msg);
        }
        sut.close();
        for (byte[] msg : messages) {
            assertArrayEquals(msg, sut.recvMessage());
        }
        assertNull(sut.recvMessage());
    }

}
