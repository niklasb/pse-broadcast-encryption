package cryptocast.server;

import cryptocast.crypto.BroadcastSchemeUserManager;
import cryptocast.crypto.BroadcastSchemeKeyManager;
import cryptocast.crypto.Encryptor;
import cryptocast.crypto.NoMoreRevocationsPossibleError;

import com.google.common.base.Optional;

import java.util.List;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Contains the data which is managed by the controller and presented by the view.
 * @param <ID> The type of the user identities
 */
public class ServerData<ID> implements Serializable {
    private static final long serialVersionUID = -4614028292663697207L;

    private Map<String, User<ID>> userByName = new HashMap<String, User<ID>>();
    private Map<ID, User<ID>> userById = new HashMap<ID, User<ID>>();
    protected BroadcastSchemeUserManager<ID> userManager;
    protected BroadcastSchemeKeyManager<ID> keyManager;
    protected List<User<ID>> users = new ArrayList<User<ID>>();
    //the amount of all users added
    private int addedUsers = 0;

    public ServerData(BroadcastSchemeUserManager<ID> userManager, 
                      BroadcastSchemeKeyManager<ID> keyManager) {
        this.userManager = userManager;
        this.keyManager = keyManager;
    }

    /**
     * Creates and saves a new user by name.
     * @param name The user's name
     * @return The new user if it has been added successfully or absent
     *         if a user with the same name already existed
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
     * @param name The user's name
     * @return A user instance, if it was found, or absent otherwise
     */
    public Optional<User<ID>> getUserByName(String name) {
        return Optional.fromNullable(userByName.get(name));
    }

    /**
     * Retrieves a user's personal key.
     * @param user The user object
     * @return The private key
     */
    public Optional<? extends PrivateKey> getPersonalKey(User<ID> user) {
        return keyManager.getPersonalKey(user.getIdentity());
    }

    public boolean revoke(User<ID> user) throws NoMoreRevocationsPossibleError {
        return userManager.revoke(user.getIdentity());
    }

    public boolean unrevoke(User<ID> user) {
        return userManager.unrevoke(user.getIdentity());
    }

    public boolean isRevoked(User<ID> user) {
        return userManager.isRevoked(user.getIdentity());
    }

    public List<User<ID>> getUsers() {
        return users;
    }
}