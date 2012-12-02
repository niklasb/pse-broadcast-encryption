package broadcastenc.server;

import java.io.File;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.Map;
import java.util.Observable;

/** Contains the data which is changed by Controller and presented by View
 */
public class ServerData extends Observable implements Serializable {

    /** Saves the mapping of Users to PrivateKeys */
    private Map<User, PrivateKey> users;
    private Collection<User> revokedUsers;
    private File streamData;
    
    /**
     * Adds a new user and its private key to the map
     * @param user The user that is added.
     * @param privateKey The assigned private key.
     */
    public void mapAddUser(User user, PrivateKey privateKey) {
        
    }
    
    /**
     * Checks if the given user is on the revoked list
     * @param user the user that is checked
     * @return true if the user is revoked, false if not
     */
    public boolean isRevoked(User user) {
        return false;
        
    }
}