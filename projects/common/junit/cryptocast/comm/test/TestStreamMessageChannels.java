package cryptocast.comm.test;

import static org.junit.Assert.*;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static cryptocast.util.ByteStringUtils.*;
import cryptocast.comm.*;

import org.junit.Test;

public class TestStreamMessageChannels {
	@Test
	public void sendAndRecvWorkTogether() throws Exception {
	    byte[][] messages = new byte[][] { str2bytes("abc"),
	                                       str2bytes("aaaaaaaaaa"),
	                                       str2bytes("XXXXXX"),
	                                       str2bytes("foobar")};
	    byte[] buffer = new byte[4096];
        PipedOutputStream pipeOut = new PipedOutputStream();
        PipedInputStream pipeIn = new PipedInputStream(pipeOut);
	    StreamMessageInChannel in = new StreamMessageInChannel(pipeIn);
	    StreamMessageOutChannel out = new StreamMessageOutChannel(pipeOut);
	    for (byte[] msg : messages) {
	        out.sendMessage(msg);
	        assertArrayEquals(msg, in.recvMessage());
	    }
	    for (byte[] msg : messages) {
            out.sendMessage(msg);
	    }
	    for (byte[] msg : messages) {
	        assertArrayEquals(msg, in.recvMessage());
        }
	}
}