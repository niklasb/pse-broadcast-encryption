package cryptocast.server;

import cryptocast.crypto.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Contains the data which is managed by the controller and presented by the view.
 * @param <ID> The type of the user identities
 */
public class ServerData<ID> implements Serializable {
    private static final long serialVersionUID = -4614028292663697207L;
    private static final Logger log = LoggerFactory.getLogger(ServerData.class);

    private Map<String, User<ID>> userByName = Maps.newHashMap();
    private Map<ID, User<ID>> userById = Maps.newHashMap();
    
    /**
     * The set of users known to the server.
     */
    protected Set<User<ID>> users = Sets.newHashSet();
    
    /**
     * Manages the users.
     */
    protected BroadcastSchemeUserManager<ID> userManager;
    
    /**
     * Manages the keys
     */
    protected BroadcastSchemeKeyManager<ID> keyManager;
    private int addedUsers = 0;

    /**
     * Creates ServerData with the given parameter.
     * 
     * @param userManager Manages the users.
     * @param keyManager Manages the keys.
     */
    public ServerData(BroadcastSchemeUserManager<ID> userManager, 
                      BroadcastSchemeKeyManager<ID> keyManager) {
        this.userManager = userManager;
        this.keyManager = keyManager;
    }
    
    /**
     * Creates and saves a new user by name.
     * 
     * @param name The user's name.
     * @return The new user if it has been added successfully or absent
     *         if a user with the same name already existed.
     */
    public Optional<User<ID>> createNewUser(String name) {
        if (userByName.get(name) != null) {
            return Optional.absent();
        }
        // create necessary data
        ID userIdent = userManager.getIdentity(addedUsers++);
        User<ID> newOne = new User<ID>(name, userIdent);
        // adjust data structures
        userByName.put(name, newOne);
        userById.put(userIdent, newOne);
        users.add(newOne);
        return Optional.of(newOne);
    }

    /**
     * Retrieves a user by name.
     * 
     * @param name The user's name
     * @return A user instance, if it was found, or absent otherwise
     */
    public Optional<User<ID>> getUserByName(String name) {
        return Optional.fromNullable(userByName.get(name));
    }

    /**
     * Retrieves a user's personal key.
     * 
     * @param user The user object.
     * @return The private key.
     */
    public Optional<? extends PrivateKey> getPersonalKey(User<ID> user) {
        return keyManager.getPersonalKey(user.getIdentity());
    }

    /**
     * Revokes users.
     * 
     * @param users The users to revoke.
     * @return  <code>true</code>, if the set of revoked users changed or <code>false</code> otherwise.
     * @throws NoMoreRevocationsPossibleError
     */
    public void revoke(Set<User<ID>> users) throws NoMoreRevocationsPossibleError {
        ImmutableSet.Builder<ID> identities = ImmutableSet.builder();
        for (User<ID> user : users) {
            identities.add(user.getIdentity());
        }
        userManager.revoke(identities.build());
    }

    /**
     * Authorizes a user.
     * 
     * @param user The user to authorize.
     * @return <code>true</code>, if the set of revoked users changed or <code>false</code> otherwise.
     */
    public void unrevoke(User<ID> user) {
        userManager.unrevoke(user.getIdentity());
    }

   /**
    * Checks whether the user is revoked.
    * 
    * @param user The user.
    * @return <code>true</code>, if the user was revoked or <code>false</code> otherwise.
    */
    public boolean isRevoked(User<ID> user) {
        return userManager.isRevoked(user.getIdentity());
    }

    /**
     * @return all users.
     */
    public Set<User<ID>> getUsers() {
        return users;
    }
}