package cryptocast.client;

import android.os.Bundle;
import cryptocast.client.filechooser.FileChooser;

/**
 * This activity lets a user choose an encryption key file
 * which is then sent to the server for authentication.
 */
public class KeyChoiceActivity extends ClientActivity {
    FileChooser fileChooser = new FileChooser(".*\\.key");
    
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        fileChooser.init(this);
    }
}
