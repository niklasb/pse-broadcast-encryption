package cryptocast.client;

import java.io.File;
import java.nio.ByteBuffer;
import cryptocast.comm.InChannel;


 /**
 * This class represents the activity to connect to the server.
 * Before connecting this activity start the {@link KeyChoiceActivity} to 
 * let the user choose an encryption key file. When the client receives a 
 * data stream the {@link StreamViewerActivity} is started to process the 
 * stream and show its contents.
 */
public class ServerLoginActivity implements InChannel {

	
	
	/**
	 * Connects to server
	 * @param serverAddress the server address
	 */
	public void connectToServer(String serverAddress) {
		
	}

	/**
	 * Checks if server address is valid
	 * @param address the server address
	 */
	private void checkValidAddress(String address) {
		
	}
	
	/**
	 * Shows activity to choose key file
	 */
	public void chooseKeyFile() {
		
	}
	
	/**
	 * Saves servers and their corresponding key files.
	 * @param serverAddress the server address
	 * @param file the key file
	 */
	public void saveServer (String serverAddress, File file) {
		
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
	

    /**
     * Receives data from the channel. It is decrypted on the fly.
     * @param size the amount of bytes to receive
     */
    public ByteBuffer recv(int size) { return null; }
}


