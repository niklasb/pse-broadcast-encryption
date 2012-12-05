package cryptocast.client;

import java.io.File;
import java.nio.ByteBuffer;
import cryptocast.comm.InChannel;

 /**
 * This class represents the activity to connect to the server.
 * Before connecting this activity start the {@link KeyChoiceActivity} to
 * let the user choose an encryption key file. When the client receives a
 * data stream the {@link StreamViewerActivity} is started to process the
 * stream and show its contents.
 */
public class ServerLoginActivity {
    /**
     * Connects to server
     * @param serverAddress the server address
     */
    public void connectToServer(String serverAddress) {
    }

    /**
     * Checks if server address is valid
     * @param address the server address
     */
    //private void checkValidAddress(String address) {
    //}

    /**
     * Shows activity to choose key file
     */
    //private File chooseKeyFile() {
        ////KeyChoiceActivity act = new KeyChoiceActivity();
        ////act.show();
        ////return act.chosenFile;
    //}

    /**
     * Saves servers and their corresponding key files.
     * @param serverAddress the server address
     * @param file the key file
     */
    //private void saveServer(String serverAddress, File file) {
    //}

    /**
     * Shows activity to view downloaded stream
     */
    //private void showStream(InChannel in_chan) {
    //}

    /**
     * Processes any errors
     */
    //public void processError(Error x) {
    //}
}


