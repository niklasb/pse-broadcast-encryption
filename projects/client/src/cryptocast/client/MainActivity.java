package cryptocast.client;

import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

 /**
 * This class represents the activity to connect to the server.
 * Before connecting this activity start an instance of {@link KeyChoiceActivity} to
 * let the user choose an encryption key file. When the client receives a
 * data stream an instance of a {@link StreamViewerActivity} is started to process the
 * stream and show its contents.
 */
public class MainActivity extends FragmentActivity {
    /**
     * Connects to a server.
     * @param view The view from which this method was called.
     */
    public void connectToServer(View view) {
    }

    /** Handles a click on the main menu.
     * @param item The clicked item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    /**
     * Checks if server address is valid.
     * @param address The server address
     */
    //private void checkValidAddress(String address) {
    //}

    /**
     * Shows activity to choose key file.
     */
    //private File chooseKeyFile() {
        ////KeyChoiceActivity act = new KeyChoiceActivity();
        ////act.show();
        ////return act.chosenFile;
    //}

    /**
     * Saves servers and their corresponding key files.
     * @param serverAddress The address of the server
     * @param file The key file
     */
    //private void saveServer(String serverAddress, File file) {
    //}

    /**
     * Shows activity to view the downloaded data.
     */
    //private void showStream(InChannel in_chan) {
    //}

    /**
     * Processes any errors.
     */
    //public void processError(Error x) {
    //}
}


