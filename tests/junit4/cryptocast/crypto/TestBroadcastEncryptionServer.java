package cryptocast.crypto;

import java.util.ArrayList;

import org.junit.*;
import static org.mockito.Mockito.*;
import org.mockito.*;

import cryptocast.crypto.naorpinkas.NaorPinkasIdentity;

class Identity { }

public class TestBroadcastEncryptionServer {
    @Mock private BroadcastSchemeUserManager<Identity> userManager;
    @Mock private DynamicCipherOutputStream cipherStream;
    private BroadcastEncryptionServer<Identity> sut;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        sut = new BroadcastEncryptionServer<Identity>(userManager, cipherStream, 0, null);
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
        ArrayList<Identity> ids = new ArrayList<Identity>();
        ids.add(new Identity());
        sut.revoke(ids);
        verify(userManager).revoke(ids);
    }

    @Test
    public void revokeTriggersKeyUpdate() throws Exception {
        ArrayList<Identity> id = new ArrayList<Identity>();
        id.add(new Identity());
        when(userManager.revoke(id))
            .thenReturn(true)
            .thenReturn(false);
        sut.revoke(id);
        verify(cipherStream, times(1)).updateKey();
        sut.revoke(id);
        verify(cipherStream, times(1)).updateKey();
        
        when(userManager.unrevoke(id.get(0)))
            .thenReturn(true)
            .thenReturn(false);
        sut.unrevoke(id.get(0));
        verify(cipherStream, times(2)).updateKey();
        sut.unrevoke(id.get(0));
        verify(cipherStream, times(2)).updateKey();
    }
}