package cryptocast.client;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import android.content.Context;
import android.view.View;

/**
 * This class is responsible for saving recently selected servers
 * and their corresponding key files.
 *
 */
public class ServerHistory implements Serializable {
    private static final long serialVersionUID = 140551767140374605L;
    public static final String FILENAME = "serverHistory";
    
    // A map for servers and their corresponding key files
    private Map<String, File> servers;
    
    //server history should only be created if it could not be loaded from storage
    private ServerHistory() { }
    
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
        
    }
    
    /**
     * Loads an instance of serverHistory with the name specified by FILENAME 
     * from the app's internal storage.
     * @return The instance of serverHistory loaded or if a new instance of serverHistory
     * if it does not exist.
     */
    public static ServerHistory loadServerHistory(Context context, String filename) {
        return null;
    }
    
    /**
     * Saves this instance at the app's internal storage using FILENAME as name of the file.
     */
    public static void saveServerHistory() {
        
    }
}
