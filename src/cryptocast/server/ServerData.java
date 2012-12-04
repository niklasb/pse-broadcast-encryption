package cryptocast.server;

import cryptocast.crypto.*;

import com.google.common.base.Optional;

import java.io.File;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Map;

/** Contains the data which is changed by Controller and presented on CLI */
public class ServerData<ID> implements Serializable {
    private File streamDir;
    private Map<String, User> user_by_name;
    private Map<ID, User> user_by_id;
    private BroadcastSchemeUserManager<ID> users;
    private BroadcastSchemeKeyManager<ID> keys;

    /**
     * Creates and saves a new user by name.
     * @param name The user's name
     */
    public Optional<User> createNewUser(String name) { return null; }

    /**
     * Retrieves a user by name
     * @param name The user's name
     * @return A user instance, if it was found, or absent otherwise
     */
    public Optional<User> getUserByName(String name) { return null; }

    /**
     * Retrieves a user's personal key
     * @param user The user object
     * @return The private key
     */
    public Optional<PrivateKey> getPersonalKey(User user) { return null; }
    
    /**
     * @return Returns the directory from which is currently streamed.
     */
    public Optional<File> getStreamDir() {
        return null;
    }
    
    /**
     * Saves the directory from which is streamed.
     * @param streamDir The directory from which is streamed.
     */
    public Optional<File> setStreamDir(File streamDir) {
        return null;
        
    }
    
    
}
