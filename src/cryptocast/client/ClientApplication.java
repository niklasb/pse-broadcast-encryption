package cryptocast.client;

import java.io.FileNotFoundException;
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
    
    private ServerHistory serverHistory;
    private static final String serverHistoryFName = "server_history";
    
    @Override
    public void onCreate() {
        try {
            InputStream in = openFileInput(serverHistoryFName);
            serverHistory = SerializationUtils.readFromStream(in);
        } catch (Exception e) {
            serverHistory = new ServerHistory();
        }
    }
    
    public void saveState(Context context) {
        try {
            OutputStream out = context.openFileOutput(serverHistoryFName, Context.MODE_PRIVATE);
            SerializationUtils.writeToStream(out, serverHistory);
        } catch (FileNotFoundException e) {
            log.error("File to save ServerHistory not found!", e);
        } catch (IOException e) {
            log.error("Cannot save server history!", e);
        }
    }
    
    public ServerHistory getHistory() {
        return serverHistory;
    }
}
