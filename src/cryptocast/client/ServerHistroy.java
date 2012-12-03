package cryptocast.client;

import java.io.File;
import java.util.Map;

/**
 * This class is responsible for saving the last selected servers
 * and their corresponding key files.
 * 
 */
public class ServerHistroy {


	/**
	 * A map for servers and their corresponding key files
	 */
	private Map<String, File> servers;

	
	/**
	 * Constructor for this class
	 */
	public ServerHistroy() {
	
	}

	/**
	 * @return the servers
	 */
	public Map<String, File> getServers() {
		return servers;
	}

	/**
	 * @param servers the servers to set
	 */
	public void setServers(Map<String, File> servers) {
		this.servers = servers;
	}

	

	
}
