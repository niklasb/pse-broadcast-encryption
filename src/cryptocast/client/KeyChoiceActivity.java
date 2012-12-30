package cryptocast.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cryptocast.client.filechooser.FileChooser;

/**
 * This activity lets a user choose an encryption key file
 * which is then sent to the server for authentication.
 */
public class KeyChoiceActivity extends FileChooser {    
    @Override
    public void onCreate(Bundle b) {
        setFileFilter(".*\\.key");
        super.onCreate(b);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /** 
     * Handles a click on the main menu.
     * @param item The clicked item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle menu item click
        switch (item.getItemId()) {
            case R.id.itemMain:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemOptions:
                intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemHelp:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemPlayer:
                intent = new Intent(this, StreamViewerActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
