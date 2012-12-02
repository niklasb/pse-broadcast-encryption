package broadcastenc.broadcastencriptionclient;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
/**
 * This activity lets a user choose an encryption key file 
 * which is then sent to the server for authentication.
 */
public class KeyChoiceActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}
	
	/**
	 * Chooses a key file
	 * @param file the key file
	 */
	public void chooseKeyFile(File file) {
		
	}
	
}
