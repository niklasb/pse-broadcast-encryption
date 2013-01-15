package cryptocast.server;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import cryptocast.crypto.NoMoreRevocationsPossibleError;
import cryptocast.crypto.naorpinkas.*;

public class TestNaorPinkasServerData {
    private SchnorrNPServer npServer = 
               (SchnorrNPServer) new SchnorrNPServerFactory().construct(10);
    private NaorPinkasServerData sut = new NaorPinkasServerData(npServer);

    @Before
    public void setUp() {
        sut.createNewUser("alice");
        sut.createNewUser("bob");
    }
    
    @Test
    public void userCreationSuccess() {
        String expected = "hans";
        Optional<User<NPIdentity>> result = sut.createNewUser(expected);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get().getName());
    }
    
    @Test
    public void userCreationFail() {
        Optional<User<NPIdentity>> result = sut.createNewUser("bob");
        assertFalse(result.isPresent());
    }
    
    @Test
    public void getExistingUser() {
        Optional<User<NPIdentity>> mUser = sut.getUserByName("alice");
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
        ImmutableSet.Builder<String> names = ImmutableSet.builder();
        for (User<NPIdentity> user : sut.getUsers()) {
            names.add(user.getName());
        }
        assertEquals(ImmutableSet.of("foo", "bob", "alice"), names.build());
    }
    
    @Test
    public void userHasKey() {
        Optional<User<NPIdentity>> mUser = sut.getUserByName("alice");
        assertTrue(mUser.isPresent());
        assertTrue(mUser.get().getIdentity() != null);
    }
    
    @Test
    public void revokeUser() {
        Optional<User<NPIdentity>> mUser = sut.getUserByName("alice");
        assertTrue(mUser.isPresent());
        try {
            sut.revoke(ImmutableSet.of(mUser.get()));
        } catch (NoMoreRevocationsPossibleError e) {
            // cannot happen
            e.printStackTrace();
        }
        assertTrue(sut.isRevoked(mUser.get()));
    }
    
    @Test
    public void unrevokeUser() {
        revokeUser();
        Optional<User<NPIdentity>> mUser = sut.getUserByName("alice");
        assertTrue(mUser.isPresent());
        sut.unrevoke(mUser.get());
        assertFalse(sut.isRevoked(mUser.get()));
    }
}