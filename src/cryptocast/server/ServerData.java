package cryptocast.server;

import cryptocast.crypto.BroadcastSchemeUserManager;
import cryptocast.crypto.BroadcastSchemeKeyManager;
import cryptocast.crypto.NoMoreRevocationsPossibleError;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Contains the data which is managed by the controller and presented by the view.
 * @param <ID> The type of the user identities
 * @param <T> The type of the model
 */
public class ServerData<ID> extends Observable implements Serializable {
    private static final long serialVersionUID = -4614028292663697207L;
    private static final Logger log = LoggerFactory.getLogger(ServerData.class);

    private Map<String, User<ID>> userByName = Maps.newHashMap();
    private Map<ID, User<ID>> userById = Maps.newHashMap();
    protected List<User<ID>> users = Lists.newArrayList();
    protected BroadcastSchemeUserManager<ID> userManager;
    protected BroadcastSchemeKeyManager<ID> keyManager;
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
        setChanged();
        notifyObservers();
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
        boolean res = userManager.revoke(user.getIdentity());
        setChanged();
        notifyObservers();
        return res;
    }

    public boolean unrevoke(User<ID> user) {
        boolean res = userManager.unrevoke(user.getIdentity());
        setChanged();
        notifyObservers();
        return res;
    }

    public boolean isRevoked(User<ID> user) {
        return userManager.isRevoked(user.getIdentity());
    }

    public List<User<ID>> getUsers() {
        return users;
    }
}