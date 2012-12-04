package cryptocast.client;

import java.io.File;
import java.util.Map;

/**
 * This class is responsible for saving recently selected servers
 * and their corresponding key files.
 *
 */
public class ServerHistory {
    /**
     * A map for servers and their corresponding key files
     */
    private Map<String, File> servers;

    /**
     * Constructor for this class
     */
    public ServerHistory() { }

    /**
     * @return the servers
     */
    public Map<String, File> getServers() { return servers; }

    /**
     * @param servers the servers to set
     */
    public void setServers(Map<String, File> servers) { this.servers = servers; }
}
