package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

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
    public void broadcastsTheCorrectNumberOfShares() throws Exception {
        assertEquals(t, server.encryptNumber(BigInteger.valueOf(111111)).getShares().size());
    }
    
    @Test
    public void canCheckRevocationStatus() throws Exception {
        ArrayList<NaorPinkasIdentity> ids = new ArrayList<NaorPinkasIdentity>();
        NaorPinkasIdentity id = server.getIdentity(2);
        ids.add(id);
        assertFalse(server.isRevoked(id));
        server.revoke(ids);
        assertTrue(server.isRevoked(id));
    }

    @Test
    public void revokeAndUnrevokeReturnCorrectBool() throws Exception {
        ArrayList<NaorPinkasIdentity> ids = new ArrayList<NaorPinkasIdentity>();
        NaorPinkasIdentity id = server.getIdentity(2);
        ids.add(id);
        assertTrue(server.revoke(ids));
        assertFalse(server.revoke(ids));
        assertTrue(server.unrevoke(id));
        assertFalse(server.unrevoke(id));
    }
    
    @Test
    public void canRevokeUpToTUsers() throws Exception {
        ArrayList<NaorPinkasIdentity> ids = new ArrayList<NaorPinkasIdentity>();
        for (int i = 0; i < t; ++i) {
            ids.add(server.getIdentity(i));
        }
        server.revoke(ids);
    }

    @Test(expected=NoMoreRevocationsPossibleError.class)
    public void canRevokeOnlyTUsers() throws Exception {
        ArrayList<NaorPinkasIdentity> ids = new ArrayList<NaorPinkasIdentity>();
        for (int i = 0; i < t + 1; ++i) {
            ids.add(server.getIdentity(i));
        }
        server.revoke(ids);
    }

    @Test
    public void broadcastsRevokedUserShares() throws Exception {
        ArrayList<NaorPinkasIdentity> ids = new ArrayList<NaorPinkasIdentity>();
        int[] revoked = { 0, 2, 4 };
        for (int i : revoked) {
            ids.add(server.getIdentity(i));
        }
        server.revoke(ids);
        NaorPinkasMessage msg = server.encryptNumber(BigInteger.valueOf(111111));
        for (int i : revoked) {
            assertTrue(containsShareForUser(msg, server.getIdentity(i)));
        }
    }
    
    @Test
    public void sendsCorrectLagrangeCoefficients() throws Exception {
        ArrayList<NaorPinkasIdentity> ids = new ArrayList<NaorPinkasIdentity>();
        for (int i = 2; i <= 4; ++i) {
            ids.add(server.getIdentity(i));
        }
        server.revoke(ids);
        server.unrevoke(server.getIdentity(3));
        ids.clear();
        ids.add(server.getIdentity(9));
        ids.add(server.getIdentity(8));
        server.revoke(ids);
        server.unrevoke(server.getIdentity(9));
        server.unrevoke(server.getIdentity(2));
        
        NaorPinkasMessage msg = server.encryptNumber(BigInteger.ONE);
        Map<BigInteger, BigInteger> coefficients = msg.getLagrange().getCoefficients();
        assertEquals(t, coefficients.size());
        for (NaorPinkasShare share : msg.getShares()) {
            assertTrue(coefficients.containsKey(share.getI()));
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
