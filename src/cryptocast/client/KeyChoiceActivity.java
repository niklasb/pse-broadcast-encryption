package cryptocast.client;

import android.os.Bundle;
import android.view.Menu;
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
