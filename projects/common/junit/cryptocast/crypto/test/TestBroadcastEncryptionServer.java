package cryptocast.crypto.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.crypto.SecretKey;

import org.junit.*;
import static org.mockito.Mockito.*;
import org.mockito.*;

import cryptocast.comm.*;
import cryptocast.crypto.*;
import static cryptocast.util.ByteUtils.*;

class Identity { }

public class TestBroadcastEncryptionServer {
    @Mock private BroadcastSchemeUserManager<Identity> userManager;
    @Mock private DynamicCipherOutputStream cipherStream;
    private BroadcastEncryptionServer<Identity> sut;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        sut = new BroadcastEncryptionServer<Identity>(userManager, cipherStream);
    }

    @Test
    public void noInteractionsAtConstruction() throws Exception {
        verifyZeroInteractions(userManager, cipherStream);
    }

    @Test
    public void keyUpdateWorks() throws Exception {
        sut.updateKey();
        verify(cipherStream).updateKey();
    }

    @Test
    public void keyBroadcastWorks() throws Exception {
        sut.broadcastKey();
        verify(cipherStream).reinitializeCipher();
    }
    
    @Test
    public void revokeInformsBackend() throws Exception {
        Identity id = new Identity();
        sut.revoke(id);
        verify(userManager).revoke(id);
    }

    @Test
    public void revokeTriggersKeyUpdate() throws Exception {
        Identity id = new Identity();
        sut.revoke(id);
        verify(cipherStream).updateKey();
    }
}
