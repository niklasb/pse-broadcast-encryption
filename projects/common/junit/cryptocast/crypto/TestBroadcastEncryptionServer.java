package cryptocast.crypto;

import static org.junit.Assert.*;

import java.io.OutputStream;

import org.junit.*;

import cryptocast.comm.MemoryOutputStream;
import cryptocast.comm.MessageOutChannel;
import static org.mockito.Mockito.*;
import static cryptocast.comm.TestUtils.*;

class Identity {
}

public class TestBroadcastEncryptionServer {

    private BroadcastEncryptionServer<Identity> sut;
    private BroadcastSchemeUserManager<Identity> userManager;
    private MemoryOutputStream payloadStream;
    private OutputCipherControl cipherControl;
    private MessageOutChannel controlChannel;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        userManager = mock(BroadcastSchemeUserManager.class);
        payloadStream = new MemoryOutputStream(4096);
        cipherControl = mock(OutputCipherControl.class);
        controlChannel = mock(MessageOutChannel.class);
        sut = new BroadcastEncryptionServer<Identity>(
            userManager, 
            null, 
            controlChannel, 
            payloadStream, 
            cipherControl);
    }

    @Test
    public void noInteractionsAtConstruction() throws Exception {
        verifyZeroInteractions(userManager, cipherControl, controlChannel);
        assertEquals(0, payloadStream.getSentBytes().length);
    }

    @Test
    public void normalWriteWorks() throws Exception {
        byte[] expected = str2bytes("abc");
        sut.write(expected);
        assertArrayEquals(expected, payloadStream.getSentBytes());
        verifyZeroInteractions(userManager, cipherControl, controlChannel);
    }

    @Test
    public void writeAfterKeyUpdateWorks() throws Exception {
        
    }
}
