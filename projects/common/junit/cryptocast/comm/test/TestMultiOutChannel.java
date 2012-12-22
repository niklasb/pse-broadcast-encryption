package cryptocast.comm.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.junit.*;

import static cryptocast.util.ByteUtils.*;
import cryptocast.comm.*;

public class TestMultiOutChannel {
    private MultiOutputStream sut =
                new MultiOutputStream(MultiOutputStream.ErrorHandling.THROW);;
    private ByteArrayOutputStream[] channels = {
            new ByteArrayOutputStream(),
            new ByteArrayOutputStream(),
            new ByteArrayOutputStream(),
        };
    
    @Before
    public void setUp() {
        for (OutputStream chan : channels) {
            sut.addChannel(chan);
        }
    }
    
    @Test
    public void sendWorks() throws Exception {
        byte[] expected = str2bytes("abcd");
        sut.write(expected);
        for (ByteArrayOutputStream chan : channels) {
            assertArrayEquals(expected, chan.toByteArray());
        }
    }
    
    @Test
    public void removeChannelWorks() throws Exception {
        sut.removeChannel(channels[0]);
        byte[] expected = str2bytes("abcd");
        sut.write(expected);
        assertEquals(0, channels[0].toByteArray().length);
        for (int i = 1; i < channels.length; ++i) {
            assertArrayEquals(expected, channels[i].toByteArray());
        }
    }
}
