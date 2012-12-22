package cryptocast.crypto.naorpinkas.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import cryptocast.crypto.SchnorrGroup;
import cryptocast.crypto.naorpinkas.NaorPinkasClient;
import cryptocast.crypto.naorpinkas.NaorPinkasServer;

public class TestNaorPinkasServer {
    @Test
    public void getPersonalKeyWorks() {
        NaorPinkasServer server = 
                NaorPinkasServer.generate(5, SchnorrGroup.getP1024Q160());
        for (int i = 0; i < 10; ++i) {
            assertTrue(server.getPersonalKey(server.getIdentity(i)).isPresent());
        }
    }
}
