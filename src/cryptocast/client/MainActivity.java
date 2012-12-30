package cryptocast.client;

import java.io.File;

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
    private static final int RESULT_KEY_CHOICE = 1;
    private static File keyFile;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        String hostname = loadHostname();
        TextView tv1 = (TextView) findViewById(R.id.editHostname);
        tv1.setText(hostname);

        if (keyFile != null) {
            startStreamViewer(hostname, keyFile);
            keyFile = null;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        storeHostname(getHostname());
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
        String hostname = getHostname();
        // TODO check if hostname is valid, look up server
        startKeyChooserForResult();
    }

    private String getHostname() {
        return ((EditText) findViewById(R.id.editHostname))
                .getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_KEY_CHOICE && resultCode == RESULT_OK) {
            // store key file so that it can be read in onResume(),
            // when the activity is fully initialized
            keyFile = new File(data.getStringExtra("chosenFile"));
        }
    }

    protected void startKeyChooserForResult() {
        Intent intent = new Intent(this, KeyChoiceActivity.class);
        startActivityForResult(intent, RESULT_KEY_CHOICE);
    }

    protected void startStreamViewer(String hostname, File keyFile) {
        Intent intent = new Intent(this, StreamViewerActivity.class);
        intent.putExtra("hostname", hostname);
        intent.putExtra("keyFile", keyFile.getAbsolutePath());
        startActivity(intent);
    }
    
    protected void storeHostname(String serverName) {
        //saving last server name to shared preference
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_server_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_server_main),
                serverName);
        editor.commit();
    }
    
    protected String loadHostname() {
        //loading last server name from shared preference
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_server_name), Context.MODE_PRIVATE);
        String serverName = sharedPref.getString(getString(R.string.saved_server_main), "");
        return serverName;
    }
}


