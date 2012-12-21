package cryptocast.crypto.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.crypto.SecretKey;

import org.junit.*;
import static org.mockito.Mockito.*;
import org.mockito.*;

import cryptocast.comm.*;
import cryptocast.crypto.*;
import static cryptocast.util.ByteStringUtils.*;

class Identity {
}

public class TestBroadcastEncryptionServer {
    @Mock private BroadcastSchemeUserManager<Identity> userManager;
    @Mock private OutputCipherControl cipherControl;
    @Mock private MessageOutChannel controlChannel;
    @Mock private Encryptor<byte[]> enc;
    @Mock private SecretKey key;
    private PipedOutputStream payloadStreamOut;
    private PipedInputStream payloadStreamIn;
    private BroadcastEncryptionServer<Identity> sut;

    private void setNextKeyEncryption(byte[] data) {
        when(key.getEncoded()).thenReturn(data);
        when(enc.encrypt(data)).thenReturn(data);
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        payloadStreamOut = new PipedOutputStream();
        payloadStreamIn = new PipedInputStream(payloadStreamOut);
        when(cipherControl.getKey()).thenReturn(key);
        sut = new BroadcastEncryptionServer<Identity>(
            userManager, 
            enc, 
            controlChannel, 
            payloadStreamOut, 
            cipherControl);
        setNextKeyEncryption(str2bytes("aaa"));
    }

    @Test
    public void noInteractionsAtConstruction() throws Exception {
        verifyZeroInteractions(userManager, cipherControl, controlChannel);
        assertEquals(0, payloadStreamIn.available());
    }

    @Test
    public void normalWriteWorks() throws Exception {
        byte[] expected = str2bytes("abc");
        sut.write(expected);
        assertPayloadBytes(expected);
        verifyZeroInteractions(userManager, cipherControl, controlChannel);
    }

    @Test
    public void keyUpdateWorks() throws Exception {
        byte[] payload = str2bytes("abc");

        byte[] key1 = str2bytes("xxx");
        setNextKeyEncryption(key1);
        sut.scheduleKeyUpdate();
        sut.write(payload);
        verify(controlChannel).sendMessage(key1);
        assertPayloadBytes(payload);
        verifyNoMoreInteractions(controlChannel);
        
        byte[] key2 = str2bytes("yyy");
        setNextKeyEncryption(key2);
        sut.scheduleKeyUpdate();
        sut.write(payload);
        verify(controlChannel).sendMessage(key2);
        assertPayloadBytes(payload);
        verifyNoMoreInteractions(controlChannel);
    }
    
    @Test
    public void revokeInformsBackend() throws Exception {
        Identity id = new Identity();
        sut.revoke(id);
        verify(userManager).revoke(id);
    }
    
    @Test
    public void keyBroadcastWorks() throws Exception {
        
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
        assertPayloadBytes(payload);
    }
    
    private void assertPayloadBytes(byte[] expected) throws IOException {
        byte[] buffer = new byte[payloadStreamIn.available()];
        payloadStreamIn.read(buffer);
        assertArrayEquals(expected, buffer);
    }
}
