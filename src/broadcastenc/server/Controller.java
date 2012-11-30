package broadcastenc.server;

import java.io.File;


/** Deals with user-interactions and therefore changes data in Model if necessary.
 */
public class Controller {
    private StreamServer streamServer;
    private ServerData data;
    //private crypto server
	
    /**
     * Trys to start a new group to which data can be sent by generating private keys
     * @param amtRevocable The amount of user which can be revoked.
     * @param amtPrivateKeys The amount of private keys which are produced.
     * @param keyDir The directory to which the keys are saved.
     */
    public void handleKeyGen(int amtRevocable, int amtPrivateKeys, File keyDir) {

    }
        
    /**
     * Adds a new user and assigns a private key to that user.
     * @param user The user that is added.
     */
    public void handleAddUser(User user) {
        
    }
        
    /**
     * Bans a user from the stream by adding it to list of revoked users
     * @param user The user that is revoked.
     */
    public void handleRevokeUser(User user) {
        
    }

    /**
     * Starts the data stream 
     * @param data The file from which the data is read
     */
    public void handleStream(File data) {
    }

}