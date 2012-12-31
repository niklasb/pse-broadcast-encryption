package cryptocast.client;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

 /**
 * This class represents the activity to connect to the server.
 * Before connecting this activity start an instance of {@link KeyChoiceActivity} to
 * let the user choose an encryption key file. When the client receives a
 * data stream an instance of a {@link StreamViewerActivity} is started to process the
 * stream and show its contents.
 */
public class MainActivity extends ClientActivity {
    private static final Logger log = LoggerFactory
            .getLogger(MainActivity.class);
    
    private static final int RESULT_KEY_CHOICE = 1;
    private static File keyFile;
    private TextView editHostname;
    
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        editHostname = (TextView) findViewById(R.id.editHostname);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        editHostname.setText(app.getHostname());

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
        app.setHostname(getHostname());
        super.onPause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Shows the KeyChoiceActivity if the hostname seems to be valid
     * @param view The view from which this method was called.
     */
    public void onConnect(View view) {
        String hostname = getHostname();
        ServerHistory history = app.getServerHistory();
        if (!checkHostname(hostname)) {
            log.debug("Button connect clicked and invalid hostname.");
            showErrorDialog(getString(R.string.invalid_hostname_text));       
        } else if (history.getServers().containsKey(hostname)) {
            log.debug("Button connect clicked and server in history.");
            startStreamViewer(hostname, history.getServers().get(hostname));
        } else {
            log.debug("Button connect clicked, hostname valid and filechooser necessary.");
            startKeyChooserForResult();
        }
    }
    
    // TODO any other criterion?
    protected boolean checkHostname(String hostname) {
        return hostname.length() > 0;
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
            log.debug("Got response from key chooser: chosenFile={}", keyFile);
            app.getServerHistory().addServer(getHostname(), keyFile);
        }
    }

    protected void startKeyChooserForResult() {
        Intent intent = new Intent(this, KeyChoiceActivity.class);
        startActivityForResult(intent, RESULT_KEY_CHOICE);
    }

    protected void startStreamViewer(String hostname, File keyFile) {
        log.debug("Starting stream viewer with: hostname={} keyfile={}", hostname, keyFile);
        Intent intent = new Intent(this, StreamViewerActivity.class);
        intent.putExtra("hostname", hostname);
        intent.putExtra("keyFile", keyFile.getAbsolutePath());
        startActivity(intent);
    }
}


