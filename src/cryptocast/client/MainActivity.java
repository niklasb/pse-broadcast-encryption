package cryptocast.client;

import cryptocast.client.filechooser.FileChooser;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

 /**
 * This class represents the activity to connect to the server.
 * Before connecting this activity start an instance of {@link KeyChoiceActivity} to
 * let the user choose an encryption key file. When the client receives a
 * data stream an instance of a {@link StreamViewerActivity} is started to process the
 * stream and show its contents.
 */
public class MainActivity extends FragmentActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        String serverName = loadServerName();
        //write server to textView
        TextView tv1 = (TextView) findViewById(R.id.editHostname);
        setContentView(R.layout.activity_main);
        tv1.setText(serverName); 
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        //get last server name
        TextView tv1 = (TextView) findViewById(R.id.editHostname);
        String serverName =  tv1.getText().toString();
        storeServerName(serverName);
    }
    
    /**
     * Connects to a server.
     * @param view The view from which this method was called.
     */
    public void connectToServer(View view) {
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    /** Handles a click on the main menu.
     * @param item The clicked item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle menu item click
        switch (item.getItemId()) {
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
    
    /**
     * Shows the KeyChoiceActivity if the hostname seems to be valid
     * @param view The view from which this method was called.
     */
    public void onConnect(View view) {
        //get text which was typed to hostname textfield
        EditText editText = (EditText) findViewById(R.id.editHostname);
        String hostname = editText.getText().toString();
        //TODO check if hostname is valid
        Intent intent = new Intent(this, FileChooser.class);
        startActivity(intent);
    }
    
    public void storeServerName(String serverName) {
        //saving last server name to shared preference
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_server_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_server_main),
                serverName);
        editor.commit();
    }
    
    public String loadServerName() {
        //loading last server name from shared preference
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_server_name), Context.MODE_PRIVATE);
        String serverName = sharedPref.getString(getString(R.string.saved_server_main), "");
        
        return serverName;
    }

    /**
     * Checks if server address is valid.
     * @param address The server address
     */
    //private void checkValidAddress(String address) {
    //}

    /**
     * Shows activity to choose key file.
     */
    //private File chooseKeyFile() {
        ////KeyChoiceActivity act = new KeyChoiceActivity();
        ////act.show();
        ////return act.chosenFile;
    //}

    /**
     * Saves servers and their corresponding key files.
     * @param serverAddress The address of the server
     * @param file The key file
     */
    //private void saveServer(String serverAddress, File file) {
    //}

    /**
     * Shows activity to view the downloaded data.
     */
    //private void showStream(InChannel in_chan) {
    //}

    /**
     * Processes any errors.
     */
    //public void processError(Error x) {
    //}
}


