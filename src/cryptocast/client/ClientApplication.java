package cryptocast.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.util.SerializationUtils;

import android.app.Application;
import android.content.Context;

/**
 * Maintains the global application state.
 */
public class ClientApplication extends Application {
    private static final Logger log = LoggerFactory
            .getLogger(ClientApplication.class);
    
    // the global application state with the respective default values
    private static class State implements Serializable {
        private static final long serialVersionUID = 6185775452467276764L;
        
        ServerHistory serverHistory = new ServerHistory();
        String hostnameInput = "";
        String portInput = "";
    }
    
    private State state;
    private static final String STATE_FILE_NAME = "cryptocast_state";

    @Override
    public void onCreate() {
        try {
            InputStream in = openFileInput(STATE_FILE_NAME);
            state = SerializationUtils.readFromStream(in);
            log.debug("Loaded application state from internal storage");
        } catch (Exception e) {
            log.warn("Could not load state from interal storage, creating new state", e);
            state = new State();
        }
    }

    /**
     * Saves the application state.
     */
    public void saveState() {
        log.debug("Saving application state to internal storage");
        try {
            OutputStream out = openFileOutput(STATE_FILE_NAME, Context.MODE_PRIVATE);
            SerializationUtils.writeToStream(out, state);
        } catch (IOException e) {
            log.error("Cannot save application state!", e);
        }
    }
    

    /**
     * Returns the hostname input.
	 * 
	 * @return The hostname input.
     */
    public String getHostnameInput() {
        return state.hostnameInput;
    }

    /**
	 * Sets the hostname input.
	 *
     * @param hostname The hostname to set.
     */
    public void setHostnameInput(String hostname) {
        state.hostnameInput = hostname;
    }

    /**
     * Returns the port input.
	 * 
	 * @return The port input.
     */
    public String getPortInput() {
        return state.portInput;
    }

    /**
	 * Sets the port input.
	 * 
	 * @param port The port to set.
     */
    public void setPortInput(String port) {
        state.portInput = port;
    }
    
    /**
     * Returns the server history.
	 * 
	 * @return The server history.
     */
    public ServerHistory getServerHistory() {
        return state.serverHistory;
    }
    
    /**
     * Returns an array of the server history host names 
     * @return the string array of host names
     */
    public String[] getServerHistoryHostnames() {
        Object[] servers = getServerHistory().getServers().keySet().toArray();
        String[] hostNames = new String[servers.length];
        for (int i = 0; i < servers.length; i++) {
            hostNames[i] = ((InetSocketAddress)servers[i]).getHostName();
        }
        return hostNames;
    }
    
    public InetSocketAddress[] getServerArray() {
        InetSocketAddress[] servers = (InetSocketAddress[]) getServerHistory().getServers().keySet().toArray();
        return servers;
    }

    public String[] getServerStringArray() {
        InetSocketAddress[] servers = getServerArray();
        String[] serverStrings = new String[servers.length];
        for (int i = 0; i < servers.length; i++) {
            serverStrings[i] = servers[i].toString();
        }
        return serverStrings;
            
        
    }
    
    /**
     * Sets the server history
     * 
     * @param serverHistory The server history to set.
     */
    public void setServerHistory(ServerHistory serverHistory) {
        state.serverHistory = serverHistory;
    }
}
