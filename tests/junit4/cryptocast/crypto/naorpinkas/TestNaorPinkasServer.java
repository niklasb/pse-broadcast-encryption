package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import cryptocast.crypto.*;

public class TestNaorPinkasServer {
    int t = 10;
    SchnorrNaorPinkasServer server = 
            (SchnorrNaorPinkasServer) new SchnorrNaorPinkasServerFactory().construct(t);
    
    @Test
    public void getPersonalKeyWorks() throws Exception {
        for (int i = 0; i < 10; ++i) {
            assertTrue(server.getPersonalKey(server.getIdentity(i)).isPresent());
        }
    }
    
    @Test
    public void broadcastsTheCorrectNumberOfShares() throws Exception {
        NaorPinkasMessage<BigInteger, SchnorrGroup> msg = 
                server.encryptMessage(new byte[] { 0x1 });
        assertEquals(t, msg.getShares().size());
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
    public void canRevokeMultipleUsers() throws Exception {
        assertTrue(server.revoke(ImmutableSet.of(server.getIdentity(2), server.getIdentity(4))));
        for (int i = 0; i < t; ++i) {
            boolean revoked = server.isRevoked(server.getIdentity(i));
            assertTrue((i == 2 || i == 4) ? revoked : !revoked);
        }
    }
    
    @Test
    public void canRevokeUpToTUsers() throws Exception {
        for (int i = 0; i < t; ++i) {
            server.revoke(server.getIdentity(i));
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
    public void broadcastsRevokedUserShares() throws Exception {
        int[] revoked = { 0, 2, 4 };
        for (int i : revoked) {
            server.revoke(server.getIdentity(i));
        }
        NaorPinkasMessage<BigInteger, SchnorrGroup> msg = 
                server.encryptMessage(new byte[] { 0x1 });
        for (int i : revoked) {
            assertTrue(containsShareForUser(msg, server.getIdentity(i)));
        }
    }
    
    @Test
    public void maintainsLagrangeCoefficients() throws Exception {
        for (int i = 2; i <= 4; ++i) {
            server.revoke(server.getIdentity(i));
        }
        server.unrevoke(server.getIdentity(3));
        server.revoke(ImmutableSet.of(server.getIdentity(9), server.getIdentity(8)));
        server.revoke(ImmutableSet.of(server.getIdentity(9), server.getIdentity(8)));
        server.revoke(server.getIdentity(8));
        server.unrevoke(server.getIdentity(9));
        server.unrevoke(server.getIdentity(2));
        server.unrevoke(server.getIdentity(2));
        server.unrevoke(server.getIdentity(2));
        
        LagrangeInterpolation<BigInteger> lagrange = server.getContext().getLagrange();
        NaorPinkasMessage<BigInteger, SchnorrGroup> msg = 
                server.encryptMessage(new byte[] { 0x1 });
        assertEquals(t, lagrange.getCoefficients().size());
        for (NaorPinkasShare<BigInteger, SchnorrGroup> share : msg.getShares()) {
            assertTrue(lagrange.getCoefficients().containsKey(share.getI()));
        }
    }
    
    private boolean containsShareForUser(NaorPinkasMessage<BigInteger, SchnorrGroup> msg, 
                                         NaorPinkasIdentity id) {
        for (NaorPinkasShare<BigInteger, SchnorrGroup> share : msg.getShares()) {
            if (share.getI().equals(id.getI())) { 
                return true; 
            }
        }
        return false;
    }
}
