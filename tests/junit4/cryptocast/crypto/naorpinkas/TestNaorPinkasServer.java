package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import cryptocast.crypto.*;

public class TestNaorPinkasServer {
    int t = 10;
    NaorPinkasServer server = 
            NaorPinkasServer.generate(t, SchnorrGroup.getP1024Q160());
    
    @Test
    public void getPersonalKeyWorks() throws Exception {
        for (int i = 0; i < 10; ++i) {
            assertTrue(server.getPersonalKey(server.getIdentity(i)).isPresent());
        }
    }
    
    @Test
    public void serverBroadcastsTheCorrectNumberOfShares() throws Exception {
        assertEquals(t, server.encryptNumber(BigInteger.valueOf(111111)).getShares().size());
    }
    
    @Test
    public void canCheckRevocationStatus() throws Exception {
        NaorPinkasIdentity id = server.getIdentity(2);
        assertFalse(server.isRevoked(id));
        server.revoke(id);
        assertTrue(server.isRevoked(id));
    }

    @Test
    public void revokeAndUnrevokeReturnCorrectBool() throws Exception {
        NaorPinkasIdentity id = server.getIdentity(2);
        assertTrue(server.revoke(id));
        assertFalse(server.revoke(id));
        assertTrue(server.unrevoke(id));
        assertFalse(server.unrevoke(id));
    }
    
    @Test
    public void canRevokeUpToTUsers() throws Exception {
        for (int i = 0; i < t; ++i) {
            server.revoke(server.getIdentity(i));
        }
    }

    @Test(expected=NoMoreRevocationsPossibleError.class)
    public void canRevokeOnlyTUsers() throws Exception {
        for (int i = 0; i < t + 1; ++i) {
            server.revoke(server.getIdentity(i));
        }
    }

    @Test
    public void serverBroadcastsRevokedUserShares() throws Exception {
        int[] revoked = { 0, 2, 4 };
        for (int i : revoked) {
            server.revoke(server.getIdentity(i));
        }
        NaorPinkasMessage msg = server.encryptNumber(BigInteger.valueOf(111111));
        for (int i : revoked) {
            assertTrue(containsShareForUser(msg, server.getIdentity(i)));
        }
    }
    
    private boolean containsShareForUser(NaorPinkasMessage msg, NaorPinkasIdentity id) {
        for (NaorPinkasShare share : msg.getShares()) {
            if (share.getIdentity().equals(id)) { 
                return true; 
            }
        }
        return false;
    }
}
