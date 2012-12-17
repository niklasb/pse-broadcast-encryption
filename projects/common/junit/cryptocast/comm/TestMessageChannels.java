package cryptocast.comm;

import static org.junit.Assert.*;

import static cryptocast.comm.BufferBackedChannel.str2bytes;

import org.junit.Test;

public class TestMessageChannels {
	@Test
	public void sendAndRecvWorkTogether() throws Exception {
	    byte[][] messages = new byte[][] { str2bytes("abc"),
	                                       str2bytes("aaaaaaaaaa"),
	                                       str2bytes("XXXXXX") };
	    BufferBackedChannel pipe = BufferBackedChannel.createPipe(4096);
	    MessageInChannel in = new MessageInChannel(pipe);
	    MessageOutChannel out = new MessageOutChannel(pipe);
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
