package cryptocast.client;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * This class is responsible for saving recently selected servers
 * and their corresponding key files.
 *
 */
public class ServerHistory implements Serializable {
    private static final long serialVersionUID = 140551767140374605L;
    
    // A map for servers and their corresponding key files
    private Map<String, File> servers = new HashMap<String, File>();
    
    /**
     * @return The servers
     */
    public Map<String, File> getServers() { return servers; }

    /**
     * Adds a server to the list of servers.
     * @param hostname The server's hostname
     * @param keyfile The keyfile the user has chosen for this server.
     */
    public void addServer(String hostname, File keyfile) { 
        servers.put(hostname, keyfile);
    }
}
