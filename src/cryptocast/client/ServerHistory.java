package cryptocast.client;

import java.io.File;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * This class is responsible for saving recently selected servers
 * and their corresponding key files.
 *
 */
public class ServerHistory implements Serializable {
    private static final Logger log = LoggerFactory
            .getLogger(ServerHistory.class);
    
    private static final long serialVersionUID = 5098699284629851284L;
    
    // A map for servers and their corresponding key files
    private Map<InetSocketAddress, File> servers = 
            new HashMap<InetSocketAddress, File>();
    
    /**
     * Returns the servers and their corresponding key files. 
     * 
     * @return The servers and their corresponding key files.
     */
    public Map<InetSocketAddress, File> getServers() {
        return servers;
    }
    
    public List<InetSocketAddress> getServerList() {
        List<InetSocketAddress> server = new ArrayList<InetSocketAddress>(servers.keySet());
        return server;
    }
    
    /**
     * Gets the key file of the given server.
     * 
     * @param addr The socket address of the server.
     * @return The key file of the corresponding server.
     */
    public Optional<File> getKeyFile(InetSocketAddress addr) {
        return Optional.fromNullable(servers.get(addr));
    }

    /**
     * Adds a server to the list of servers.
     * @param addr The socket address of the server.
     * @param keyfile The keyfile the user has chosen for this server.
     */
    public void addServer(InetSocketAddress addr, File keyFile) {
        log.debug("Associating server {} with key file {}", addr, keyFile);
        servers.put(addr, keyFile);
    }
    
    
    /**
     * Removes a saved key file of the server.
     * 
     * @param addr The socket address of the server.
     */
    public void invalidateKeyFile(InetSocketAddress addr) {
        log.debug("Removing saved key file for server {}", addr);
        servers.remove(addr);
    }
}