package cryptocast.crypto;

import java.util.Observable;

import org.junit.*;
import static org.mockito.Mockito.*;
import org.mockito.*;

class Identity { }

public class TestBroadcastEncryptionServer {
    @Mock private BroadcastSchemeUserManager<Identity> userManager;
    @Mock private Observable userManagerObs;
    @Mock private DynamicCipherOutputStream cipherStream;
    private BroadcastEncryptionServer<Identity> sut;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        sut = new BroadcastEncryptionServer<Identity>(userManager, cipherStream, 0, null);
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
    public void revokeTriggersKeyUpdate() throws Exception {
        sut.update(userManagerObs, null);
        verify(cipherStream, times(1)).updateKey();
    }
}