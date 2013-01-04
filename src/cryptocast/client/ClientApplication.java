package cryptocast.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.util.SerializationUtils;

import android.app.Application;
import android.content.Context;

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

    public void saveState() {
        log.debug("Saving application state to internal storage");
        try {
            OutputStream out = openFileOutput(STATE_FILE_NAME, Context.MODE_PRIVATE);
            SerializationUtils.writeToStream(out, state);
        } catch (IOException e) {
            log.error("Cannot save application state!", e);
        }
    }
    

    public String getHostnameInput() {
        return state.hostnameInput;
    }

    public void setHostnameInput(String hostname) {
        state.hostnameInput = hostname;
    }

    public String getPortInput() {
        return state.portInput;
    }

    public void setPortInput(String port) {
        state.portInput = port;
    }
    
    public ServerHistory getServerHistory() {
        return state.serverHistory;
    }

    public void setServerHistory(ServerHistory serverHistory) {
        state.serverHistory = serverHistory;
    }
}