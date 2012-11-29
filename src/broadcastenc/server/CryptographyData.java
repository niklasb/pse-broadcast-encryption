package broadcastenc.server;

/** Contains the data which is changed by Controller and presented by View
 */
public class CryptographyData implements Serializable {

    /** Saves the mapping of Users to PrivateKeys
     */
    private HashMap<User, PrivateKey> users;
    private ArrayList<User> revoked;
    
    /**
     * Adds a new user and its private key to the map
     * @param user The user that is added.
     * @param privateKey The assigned private key.
     * @return true if the user was added, false else
     */
    public boolean mapAddUser(User user, PrivateKey privateKey) {
        
    }
    
    /**
     * Checks if the given user is on the revoked list
     * @param user the user that is checked
     * @return true if the user is revoked, false if not
     */
    public boolean isRevoked(User user) {
        
    }
}