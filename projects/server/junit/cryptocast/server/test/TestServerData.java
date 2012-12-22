package cryptocast.server.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

import cryptocast.crypto.SchnorrGroup;
import cryptocast.crypto.naorpinkas.*;
import cryptocast.server.*;

public class TestServerData {
    
    private ServerData<NaorPinkasIdentity> testData;
    
    @Before
    public void setUp() {
        NaorPinkasServer naorServer = NaorPinkasServer.generate(50, SchnorrGroup.getP1024Q160());
        testData = new ServerData<NaorPinkasIdentity>(naorServer, naorServer);
    }
    
    @Test
    public void userCreationWithNull() {
        Optional<User<NaorPinkasIdentity>> result = testData.createNewUser(null);
        assertFalse(result.isPresent());
    }
    
    @Test
    public void userCreationSuccess() {
        String expected = "hans12";
        Optional<User<NaorPinkasIdentity>> result = testData.createNewUser(expected);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get().getName());
    }
    
    @Test
    public void getNonExistingUser() {
        assertFalse(true);
    }

}
