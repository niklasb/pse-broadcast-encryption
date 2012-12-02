package broadcastenc.broadcastencriptionclient;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
/**
 * This activity is responsible for decrypting the received data
 * stream and viewing it. 
 */
public class StreamViewerActivity extends Activity {

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
	 * Processes the input stream of data
	 * @param inputStream the data stream
	 */
	public void processStream(InputStream inputStream) {
		
	}
}
