package cryptocast.client;

import java.io.File;

import cryptocast.client.fileChooser.FileChooser;

import android.support.v4.app.FragmentActivity;


/**
 * This activity lets a user choose an encryption key file
 * which is then sent to the server for authentication.
 */
public class KeyChoiceActivity extends FileChooser {
    private File chosenFile;

    /**
     * Chooses a key file
     * @param view The view from which this method was called.
     */
    public void chooseKeyFile(View view) {
        //chosenFile = file;
    }
}
