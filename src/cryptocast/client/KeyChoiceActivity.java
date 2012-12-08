package cryptocast.client;

import java.io.File;

import cryptocast.client.fileChooser.FileChooser;
import cryptocast.client.fileChooser.ListElement;

import android.support.v4.app.FragmentActivity;


/**
 * This activity lets a user choose an encryption key file
 * which is then sent to the server for authentication.
 */
public class KeyChoiceActivity extends FileChooser {
    private File chosenFile;
    
    @Override
    private void onFileClick(ListElement o) {
        
    }
}
