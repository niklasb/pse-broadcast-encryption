package cryptocast.crypto.test;

import static org.junit.Assert.*;

import javax.crypto.SecretKey;

import org.junit.*;
import static org.mockito.Mockito.*;

import cryptocast.comm.*;
import cryptocast.crypto.*;
import static cryptocast.util.ByteStringUtils.*;

class Identity {
}

public class TestBroadcastEncryptionServer {

    private BroadcastEncryptionServer<Identity> sut;
    private BroadcastSchemeUserManager<Identity> userManager;
    private MemoryOutputStream payloadStream;
    private OutputCipherControl cipherControl;
    private MessageOutChannel controlChannel;
    private Encryptor<byte[]> enc;
    private SecretKey key;

    private void setNextKeyEncryption(byte[] data) {
        when(key.getEncoded()).thenReturn(data);
        when(enc.encrypt(data)).thenReturn(data);
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        userManager = mock(BroadcastSchemeUserManager.class);
        payloadStream = new MemoryOutputStream(4096);
        cipherControl = mock(OutputCipherControl.class);
        key = mock(SecretKey.class);
        when(cipherControl.getKey()).thenReturn(key);
        controlChannel = mock(MessageOutChannel.class);
        enc = mock(Encryptor.class);
        sut = new BroadcastEncryptionServer<Identity>(
            userManager, 
            enc, 
            controlChannel, 
            payloadStream, 
            cipherControl);
        setNextKeyEncryption(str2bytes("aaa"));
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
    public void keyUpdateWorks() throws Exception {
        String payload = "abc";

        byte[] key1 = str2bytes("xxx");
        setNextKeyEncryption(key1);
        sut.scheduleKeyUpdate();
        sut.write(str2bytes(payload));
        verify(controlChannel).sendMessage(key1);
        assertArrayEquals(str2bytes(payload), payloadStream.getSentBytes());
        verifyNoMoreInteractions(controlChannel);
        
        byte[] key2 = str2bytes("yyy");
        setNextKeyEncryption(key2);
        sut.scheduleKeyUpdate();
        sut.write(str2bytes(payload));
        verify(controlChannel).sendMessage(key2);
        assertArrayEquals(str2bytes(payload + payload), 
                          payloadStream.getSentBytes());
        verifyNoMoreInteractions(controlChannel);
    }
    
    @Test
    public void revokeInformsBackend() throws Exception {
        Identity id = new Identity();
        sut.revoke(id);
        verify(userManager).revoke(id);
    }
    
    @Test
    public void revokeTriggersKeyUpdate() throws Exception {
        byte[] key1 = str2bytes("xxx");
        Identity id = new Identity();
        setNextKeyEncryption(key1);
        sut.revoke(id);
        byte[] payload = str2bytes("abc");
        sut.write(payload);
        verify(controlChannel).sendMessage(key1);
        assertArrayEquals(payload,
                payloadStream.getSentBytes());
    }
}
