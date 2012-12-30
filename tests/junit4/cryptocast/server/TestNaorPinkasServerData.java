package cryptocast.server;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.base.Optional;

import cryptocast.crypto.SchnorrGroup;
import cryptocast.crypto.naorpinkas.*;

public class TestNaorPinkasServerData {
    private NaorPinkasServer npServer = 
               NaorPinkasServer.generate(10, SchnorrGroup.getP1024Q160());
    private NaorPinkasServerData sut = new NaorPinkasServerData(npServer);

    @Before
    public void setUp() {
        sut.createNewUser("alice");
        sut.createNewUser("bob");
    }
    
    @Test
    public void userCreationSuccess() {
        String expected = "hans";
        Optional<User<NaorPinkasIdentity>> result = sut.createNewUser(expected);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get().getName());
    }
    
    @Test
    public void getExistingUser() {
        Optional<User<NaorPinkasIdentity>> mUser = sut.getUserByName("alice");
        assertTrue(mUser.isPresent());
        assertEquals("alice", mUser.get().getName());
    }
    
    @Test
    public void getNonExistingUser() {
        assertFalse(sut.getUserByName("foobar").isPresent());
    }

    @Test
    public void getUsers() {
        assertEquals(2, sut.getUsers().size());
        sut.createNewUser("foo");
        assertEquals(3, sut.getUsers().size());
        assertEquals("bob", sut.getUsers().get(1).getName());
    }
}