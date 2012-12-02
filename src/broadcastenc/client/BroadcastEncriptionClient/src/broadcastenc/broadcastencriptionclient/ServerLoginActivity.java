package broadcastenc.broadcastencriptionclient;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
/**
 * This class represents the activity to connect to the server.
 * Before connecting this activity start the {@link KeyChoiceActivity} to 
 * let the user choose an encryption key file. When the client receives a 
 * data stream the {@link StreamViewerActivity} is started to process the 
 * stream and show its contents.
 */
public class ServerLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_server_login, menu);
		return true;
	}
	
	/**
	 * Connects to server
	 * @param serverAddress
	 */
	public void connectToServer(String serverAddress) {
		
	}

	/**
	 * Checks if server address is valid
	 * @param address the server address
	 */
	public void checkValidAddress(String address) {
		
	}
	
	/**
	 * Shows activity to choose key file
	 */
	public void chooseKeyFile() {
		
	}
	
	/**
	 * Shows activity to view downloaded stream
	 */
	public void showStream() {
		
	}
	
	/**
	 * Processes any errors
	 */
	public void processError() {
		
	}
}
