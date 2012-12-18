package cryptocast.comm;

import static org.junit.Assert.*;

import static cryptocast.comm.TestUtils.*;

import org.junit.Test;

public class TestMessageChannels {
	@Test
	public void sendAndRecvWorkTogether() throws Exception {
	    byte[][] messages = new byte[][] { str2bytes("abc"),
	                                       str2bytes("aaaaaaaaaa"),
	                                       str2bytes("XXXXXX") };
	    byte[] buffer = new byte[4096];
	    MemoryInputStream pipeIn = new MemoryInputStream(buffer, 2);
	    MemoryOutputStream pipeOut = new MemoryOutputStream(buffer);
	    MessageInChannel in = new MessageInChannel(pipeIn);
	    MessageOutChannel out = new MessageOutChannel(pipeOut);
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
