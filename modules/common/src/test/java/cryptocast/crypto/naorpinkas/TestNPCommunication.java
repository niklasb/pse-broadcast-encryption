package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import org.junit.Test;

import cryptocast.crypto.*;
import static cryptocast.util.ByteUtils.str2bytes;

public abstract class TestNPCommunication<
          T, G extends CyclicGroupOfPrimeOrder<T>> {
    protected abstract NPServer<T, G> makeServer(int t);
    protected abstract NPClient<T, G> makeClient(NPKey<T, G> key);
    
    private int t = 10;
    protected NPServer<T, G> server = makeServer(t);
    protected NPClient<T, G> client = 
            makeClient(server.getPersonalKey(server.getIdentity(0)).get());

    @Test
    public void encryptDecryptByteArrayWorks() throws Exception {
        byte[][] secrets = {
               str2bytes("\u00ff\u00ff\u00ff\u00ff"),
               str2bytes("\u0000\u0000\u0000\u0000"),
               str2bytes("aaaabbbbccccdddd"), // 128 bit!
        };
        for (byte[] secret : secrets) {
            assertArrayEquals(secret, client.decrypt(server.encrypt(secret)));
        }
    }
    
    @Test(expected=InsufficientInformationError.class)
    public void decryptDoesntWorkWithRevokedShare() throws Exception {
        server.revoke(server.getIdentity(0));
        client.decrypt(server.encrypt(new byte[] { 0x1 }));
    }
}
