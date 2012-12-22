package cryptocast.crypto.naorpinkas.test;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
import static cryptocast.util.ByteUtils.str2bytes;

public class TestNaorPinkasCommunication extends TestWithNaorPinkasContext {
    private int t = 10;
    NaorPinkasServer server = NaorPinkasServer.generate(t, schnorr);
    NaorPinkasClient client = 
            new NaorPinkasClient(server.getPersonalKey(server.getIdentity(0)).get());
    
    @Test
    public void encryptDecryptNumberWorks() throws Exception {
        BigInteger secret = BigInteger.valueOf(111111);
        assertEquals(secret, client.decryptNumber(server.encryptNumber(secret)));
    }

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
        client.decryptNumber(server.encryptNumber(BigInteger.valueOf(111)));
    }
}