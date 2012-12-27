package cryptocast.client;

import java.io.File;

import cryptocast.client.filechooser.FileListElement;
import com.google.common.base.Optional;

/**
 * This activity lets a user choose an encryption key file
 * which is then sent to the server for authentication.
 */
public class KeyChoiceActivity extends cryptocast.cryptocast.client.filechooser.FileChooser {
    private File chosenFile;

    /** @return The chosen file or absent on abort. */
    public Optional<File> getChosenFile() {
        return null;
    }

    /** Called when the user clicks a file in the list.
     * @param item The clicked list item.
     */
    @Override
    protected void onFileClick(FileListElement o) {
        
    }
}
