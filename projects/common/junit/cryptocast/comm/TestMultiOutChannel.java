package cryptocast.comm;

import static org.junit.Assert.*;
import org.junit.*;

import static cryptocast.comm.BufferBackedChannel.str2bytes;

public class TestMultiOutChannel {

    private MultiOutChannel sut;
    private BufferBackedChannel[] channels;
    
    @Before
    public void setUp() {
        channels = new BufferBackedChannel[] {
            BufferBackedChannel.createOutChannel(4096),
            BufferBackedChannel.createOutChannel(4096),
            BufferBackedChannel.createOutChannel(4096),
        };
        sut = new MultiOutChannel();
        for (OutChannel chan : channels) {
            sut.addChannel(chan);
        }
    }
    
    @Test
    public void sendWorks() throws Exception {
        byte[] expected = str2bytes("abcd");
        sut.send(expected);
        for (BufferBackedChannel chan : channels) {
            assertArrayEquals(expected, chan.getSentBytes());
        }
    }
    
    @Test
    public void removeChannelWorks() throws Exception {
        sut.removeChannel(channels[0]);
        byte[] expected = str2bytes("abcd");
        sut.send(expected);
        assertEquals(0, channels[0].getSentBytes().length);
        for (int i = 1; i < channels.length; ++i) {
            assertArrayEquals(expected, channels[i].getSentBytes());
        }
    }

}
