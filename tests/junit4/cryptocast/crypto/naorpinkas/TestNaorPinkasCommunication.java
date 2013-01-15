package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import org.junit.Test;

import cryptocast.crypto.*;
import static cryptocast.util.ByteUtils.str2bytes;

public class TestNaorPinkasCommunication extends WithNaorPinkasContext {
    private int t = 10;
    SchnorrNaorPinkasServer server = 
            (SchnorrNaorPinkasServer) new SchnorrNaorPinkasServerFactory().construct(t);
    SchnorrNaorPinkasClient client = 
            new SchnorrNaorPinkasClient(server.getPersonalKey(server.getIdentity(0)).get());

    @Test
    public void encryptDecryptByteArrayWorks() throws Exception {
        byte[][] secrets = {
               str2bytes("\u00ff\u00ff\u00ff\u00ff"),
               str2bytes("\u0000\u0000\u0000\u0000"),
               str2bytes("abcdefghijklmnopqrstuvwxyz"),
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