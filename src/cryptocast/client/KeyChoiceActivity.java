package cryptocast.client;

import java.io.File;


/**
 * This activity lets a user choose an encryption key file
 * which is then sent to the server for authentication.
 */
public class KeyChoiceActivity {
    private File chosenFile;

    /**
     * Chooses a key file
     * @param file the key file
     */
    public void chooseKeyFile(File file) {
        chosenFile = file;
    }
}
