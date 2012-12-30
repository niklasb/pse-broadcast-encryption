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
    private TextView editHostname;
    private SharedPreferences sharedPrefs;
    private ClientApplication app;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editHostname = (TextView) findViewById(R.id.editHostname);
        sharedPrefs = getSharedPreferences(getString(R.string.preference_server_name), 
                                          Context.MODE_PRIVATE);
        app = ((ClientApplication) getApplication());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadUI();

        if (keyFile != null) {
            // this is a signal by onActivityResult which is called after
            // a keyfile was selected. we now have all information to
            // start the stream
            startStreamViewer(getHostname(), keyFile);
            keyFile = null;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        app.saveState();
        storeUI();
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

    protected String getHostname() {
        return editHostname.getText().toString();
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
    
    protected void storeUI() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        // store hostname
        editor.putString(getString(R.string.saved_server_main), getHostname());
        editor.commit();
    }
    
    protected void loadUI() {
        // load hostname
        String hostname = sharedPrefs.getString(getString(R.string.saved_server_main), "");
        editHostname.setText(hostname);
    }
}


