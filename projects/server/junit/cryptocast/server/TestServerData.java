package cryptocast.server;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

import cryptocast.crypto.naorpinkas.NaorPinkasClient;
import cryptocast.crypto.naorpinkas.NaorPinkasIdentity;
import cryptocast.crypto.naorpinkas.NaorPinkasServer;

public class TestServerData {
    
    private ServerData<NaorPinkasIdentity> testData;
    
    @Before
    public void setUp() {
        NaorPinkasServer naorServer = new NaorPinkasServer();
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
        //TODO !!!!!!
        Optional<User<NaorPinkasIdentity>> result = testData.getUserByName("bert");
        assertFalse(result.isPresent());
    }

}
