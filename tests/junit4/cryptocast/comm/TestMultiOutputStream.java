package cryptocast.comm;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.*;

import static cryptocast.util.ByteUtils.*;

public class TestMultiOutputStream {
    private MultiOutputStream sut =
                new MultiOutputStream();
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
    
    @Test
    public void autoRemoveWorks() throws Exception {
        MultiOutputStream sut = new MultiOutputStream(MultiOutputStream.removeOnError);
        OutputStream goodStream = mock(OutputStream.class),
                     badStream = mock(OutputStream.class);
        doThrow(IOException.class)
             .when(badStream).write(any(byte[].class), anyInt(), anyInt());
        doThrow(IOException.class)
             .when(badStream).write(any(byte[].class));
        doThrow(IOException.class)
             .when(badStream).write(anyByte());
        sut.addChannel(goodStream);
        sut.addChannel(badStream);
        assertEquals(2, sut.getChannels().size());
        sut.write(new byte[0], 0, 0);
        assertEquals(1, sut.getChannels().size());
    }
}
