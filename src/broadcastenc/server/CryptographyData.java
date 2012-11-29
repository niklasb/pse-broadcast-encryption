package broadcastenc.server;

/** Contains the data which is changed by Controller and presented by View
 */
public class CryptographyData implements Serializable {

	/** Saves the mapping of Users to PrivateKeys
	*/
	private HashMap<User, PrivateKey> users;
	private ArrayList<User> revoked;
	
}