package cryptocast.comm.test;

import static org.junit.Assert.*;

import java.io.OutputStream;

import org.junit.*;

import static cryptocast.util.ByteStringUtils.*;

public class TestMultiOutChannel {

    private MultiOutputStream sut;
    private MemoryOutputStream[] channels;
    
    @Before
    public void setUp() {
        channels = new MemoryOutputStream[] {
            new MemoryOutputStream(4096),
            new MemoryOutputStream(4096),
            new MemoryOutputStream(4096),
        };
        sut = new MultiOutputStream(MultiOutputStream.ErrorHandling.THROW);
        for (OutputStream chan : channels) {
            sut.addChannel(chan);
        }
    }
    
    @Test
    public void sendWorks() throws Exception {
        byte[] expected = str2bytes("abcd");
        sut.write(expected);
        for (MemoryOutputStream chan : channels) {
            assertArrayEquals(expected, chan.getSentBytes());
        }
    }
    
    @Test
    public void removeChannelWorks() throws Exception {
        sut.removeChannel(channels[0]);
        byte[] expected = str2bytes("abcd");
        sut.write(expected);
        assertEquals(0, channels[0].getSentBytes().length);
        for (int i = 1; i < channels.length; ++i) {
            assertArrayEquals(expected, channels[i].getSentBytes());
        }
    }
}
