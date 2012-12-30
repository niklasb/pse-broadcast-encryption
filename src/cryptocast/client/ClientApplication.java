package cryptocast.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.util.SerializationUtils;

import android.app.Application;
import android.content.Context;

public class ClientApplication extends Application {
    private static final Logger log = LoggerFactory
            .getLogger(ClientApplication.class);
    
    private ClientState state;
    private static final String STATE_FILE_NAME = "cryptocast_state";
    
    @Override
    public void onCreate() {
        try {
            InputStream in = openFileInput(STATE_FILE_NAME);
            state = SerializationUtils.readFromStream(in);
            log.debug("Loaded application state from internal storage");
        } catch (Exception e) {
            log.warn("Could not load state from interal storage, creating new state", e);
            state = new ClientState();
        }
        assert state != null;
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
    
    public ClientState getState() {
        return state;
    }
}
