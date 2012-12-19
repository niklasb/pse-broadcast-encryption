package cryptocast.server;

import cryptocast.crypto.BroadcastSchemeUserManager;
import cryptocast.crypto.BroadcastSchemeKeyManager;

import com.google.common.base.Optional;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/** Contains the data which is managed by the controller and presented by the view.
 * @param <ID> The type of the user identities
 */
public class ServerData<ID> implements Serializable {
    private Map<String, User<ID>> userByName = new HashMap<String, User<ID>>();
    private Map<ID, User<ID>> userById = new HashMap<ID, User<ID>>();
    private BroadcastSchemeUserManager<ID> users;
    private BroadcastSchemeKeyManager<ID> keys;
    //the amount of all users added
    private int addedUsers = 0;
    
    //TODO
    public ServerData(BroadcastSchemeUserManager<ID> users, BroadcastSchemeKeyManager<ID> keys) {
        this.users = users;
        this.keys = keys;
    }

    /**
     * Creates and saves a new user by name.
     * @param name The user's name
     * @return The new user if he has been added successfully, else absent is returned.
     */
    public Optional<User<ID>> createNewUser(String name) {
        if (name == null || getUserByName(name).isPresent()) {
            return Optional.absent();
        }
        // create necessary data
        ID userIdent = users.getIdentity(addedUsers);
        User<ID> newOne = new User<ID>(name, userIdent);
        // adjust data structures
        addedUsers++;
        userByName.put(name, newOne);
        userById.put(userIdent, newOne);
        
        return Optional.of(newOne);
    }

    /**
     * Retrieves a user by name.
     * @param name The user's name
     * @return A user instance, if it was found, or absent otherwise
     */
    public Optional<User<ID>> getUserByName(String name) {
        if (name == null || !userByName.containsKey(name)) {
            return Optional.absent();
        }
        return Optional.of(userByName.get(name));
    }

    /**
     * Retrieves a user's personal key.
     * @param user The user object
     * @return The private key
     */
    public Optional<PrivateKey> getPersonalKey(User<ID> user) {
        return null;
    }
}
