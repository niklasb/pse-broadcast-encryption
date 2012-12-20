package cryptocast.comm.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import static cryptocast.util.ByteStringUtils.*;
import cryptocast.comm.*;

public class TestRawMessageInChannel {
    @Test
    public void recvMessageWorks() throws Exception {
        byte[] packed = str2bytes(
                "\u0000\u0000\u0000\u0003abc\u0000\u0000\u0000\u0005defgh");
        InputStream in = new MemoryInputStream(packed, 5);
        StreamMessageInChannel msg = new StreamMessageInChannel(in);
        
        assertArrayEquals(str2bytes("abc"), msg.recvMessage());
        assertArrayEquals(str2bytes("defgh"), msg.recvMessage());
    }
    
    @Test
    public void recvMessageDetectsEOF() throws Exception {
        InputStream in = new MemoryInputStream(new byte[0], 1);
        StreamMessageInChannel msg = new StreamMessageInChannel(in);
        assertNull(msg.recvMessage());
    }
    
    @Test(expected=IOException.class)
    public void detectsMalformedMessageSize() throws Exception {
        InputStream in = new MemoryInputStream(new byte[] { 0 }, 1);
        StreamMessageInChannel msg = new StreamMessageInChannel(in);
        msg.recvMessage();
    }
    
    @Test(expected=IOException.class)
    public void detectsMalformedMessage() throws Exception {
        byte[] packed = new byte[] { 0,0,0,2,1 };
        InputStream in = new MemoryInputStream(packed, 1);
        StreamMessageInChannel msg = new StreamMessageInChannel(in);
        msg.recvMessage();
    }
}
