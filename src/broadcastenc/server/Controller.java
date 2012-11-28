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
	
	public boolean handleAddUser(User user) {
		
	}
	
	public boolean handleRevokeUser(User user) {
		
	}
	
	public boolean handleStream(File data) {
		
	}
    
}