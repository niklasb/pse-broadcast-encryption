package broadcastenc.server;

/** Deals with user-interactions and therefore changes data in Model if necessary.
 */
public class Controller {
	
    /**
     * Trys to start a new group to which data can be sent by generating private keys
     * @param amtRevocable The amount of user which can be revoked.
     * @param amtPrivateKeys The amount of private keys which are produced.
     * @param keyDir The directory to which the keys are saved.
     * @return true if the keys could be generated, else false
     */
    public boolean handleKeyGen(int amtRevocable, int amtPrivateKeys, File keyDir) {

    }
        
    /**
     * Adds a new user and assigns a private key to that user.
     * @param user The user that is added.
     * @return true if the user was added, false else
     */
    public boolean handleAddUser(User user) {
        
    }
        
    /**
     * Bans a user from the stream by adding it to list of revoked users
     * @param user The user that is revoked.
     * @return true if the user was revoked, false else
     */
    public boolean handleRevokeUser(User user) {
        
    }

    /**
     * Starts the data stream 
     * @param data The file from which the data is read
     * @return true if the stream is started successfull, false else
     */
    public boolean handleStream(File data) {
        
    }

}