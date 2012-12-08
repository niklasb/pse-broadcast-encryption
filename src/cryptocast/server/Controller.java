package cryptocast.server;

import java.io.File;
import cryptocast.comm.OutChannel;


/** Deals with user-interactions and therefore changes data in Model if necessary.
 * @param <ID> The type of the user identities
 */
public class Controller<ID> {

    private ServerData<ID> data;
    private Shell<ID> shell;
    private OutChannel outChannel;
    
    /**
     * Initializes a new controller with the given arguments.
     * @param data The data administrated by this controller.
     * @param shell The operator interface from which this controller gets its input.
     * @param outChannel The channel to which the data is send.
     */
    public Controller(ServerData<ID> data, Shell<ID> shell, OutChannel outChannel) {
        super();
        this.outChannel = outChannel;
        this.data = data;
        this.shell = shell;
    }
    
    /**
     * Initializes the server on start by loading data from a file.
     */
    public void init() {
        
    }
	
    /**
     * Tries to start a new group to which data can be sent by generating private keys.
     * @param amtRevocable The amount of user which can be revoked.
     * @param amtPrivateKeys The amount of private keys which are produced.
     * @param keyDir The directory to save the keyfiles in.
     */
    public void keyGen(int amtRevocable, int amtPrivateKeys, File keyDir) {

    }
        
    /**
     * Adds a new user and assigns a private key to that user.
     * @param user The user that is added.
     */
    public void addUser(User<ID> user) {
        
    }
        
    /**
     * Bans a user from the stream by adding it to the list of revoked users.
     * @param user The user that is revoked.
     */
    public void revokeUser(User<ID> user) {
        
    }
    
    /**
     * Authorizes a user to watch the stream by removing it from the list of revoked users.
     * @param user The user that is unbanned.
     */
    public void authorizeUser(User<ID> user) {
        
    }

    /**
     * Starts the data stream 
     * @param data The file from which the data is read
     */
    public void stream(File data) {
    }
    
    /**
     * Prints information about traffic
     */
    public void showStatistics() {
        
    }
    
    /**
     * Prints users and the keys assigned to them.
     */
    public void showUsers() {
        
    }
    
    /**
     * Prints information about the data which is currently sent.
     */
    public void showInfo() {
        //prints directory, time sending amount of revoked users/registered users...
    }

}
