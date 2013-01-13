package cryptocast.client;

import java.io.File;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private TextView editHostname, editPort;
    private File keyFile;
    private InetSocketAddress addr;
    
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        editHostname = (TextView) findViewById(R.id.editHostname);
        editPort = (TextView) findViewById(R.id.editPort);
        Object[] servers = app.getServerHistory().getServers().keySet().toArray();
        String[] hosts = new String[servers.length];
        for (int i = 0; i < servers.length; i++) {
            hosts[i] = ((InetSocketAddress)servers[i]).getHostName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hosts);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.editHostname);
        textView.setAdapter(adapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        editHostname.setText(app.getHostnameInput());
        editPort.setText(app.getPortInput());

        if (keyFile != null) {
            // this is a signal by onActivityResult which is called after
            // a keyfile was selected. we now have all information to
            // start the stream
            startStreamViewer(addr, keyFile);
            // save connection info for the future
            app.getServerHistory().addServer(addr, keyFile);
            addr = null;
            keyFile = null;
        }
    }
    
    @Override
    protected void onPause() {
        app.setHostnameInput(getHostnameInput());
        app.setPortInput(getPortInput());
        super.onPause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Validates the input and connects to the specified server,
     * possibly after letting the user choose a key file.
     * @param view The view from which this method was called.
     */
    public void onConnect(View view) {
        Optional<InetSocketAddress> mAddr = getConnectAddr();
        if (!mAddr.isPresent()) {
            showErrorDialog("Invalid hostname/port");
            return;
        }
        // TODO what if you don't have a network?
        if (mAddr.get().isUnresolved()) {
            showErrorDialog("Could not resolve hostname!");
            return;
        }
        Optional<File> mFile = app.getServerHistory().getKeyFile(mAddr.get());
        if (mFile.isPresent()) {
            log.debug("Server already in history, using key file {}", mFile.get());
            startStreamViewer(mAddr.get(), mFile.get());
        } else {
            log.debug("Server unknown, asking for key file");
            // save temporarily, so we can extract it in onResume
            this.addr = mAddr.get();
            startKeyChooserForResult();
        }
    }
    
    /**
     * Gets the socket address and checks, if the hostname and the port number are valid.
     * @return The socket address.
     */
    protected Optional<InetSocketAddress> getConnectAddr() {
        String hostname = getHostnameInput();
        int port;
        try {
            port = Integer.parseInt(getPortInput());
        } catch (NumberFormatException e) {
            return Optional.absent();
        }
        if (!checkHostname(hostname) || !checkPort(port)) {
            return Optional.absent();
        }
        return Optional.of(new InetSocketAddress(hostname, port));
    }
    
    // TODO any other criterion?
    /**
     * Checks whether the hostname is correct.
     * 
     * @param hostname The hostname.
     * @return <code>true</code>, if the hostname is correct; <code>false</code> otherwise.
     */
    protected boolean checkHostname(String hostname) {
        return hostname.length() > 0;
    }
    
    /**
    * Checks whether the port number is correct.
    * 
    * @param port The port number.
    * @return <code>true</code>, if the the port number; <code>false</code> otherwise.
    */
    protected boolean checkPort(int port) {
        return port > 0 && port < 0x10000;
    }

    /**
     * Displays text to the user and optionally allows them to edit it.
	 * 
	 * @return String representation of the hostname.
     */
    protected String getHostnameInput() {
        return editHostname.getText().toString();
    }
    
    /**
     * Displays text to the user and optionally allows them to edit it.
	 * 
	 * @return String representation of the port number.
     */
    protected String getPortInput() {
        return editPort.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_KEY_CHOICE && resultCode == RESULT_OK) {
            log.debug("Got response from key chooser: chosenFile={}", keyFile);
            // store key file so that it can be read in onResume(),
            // when the activity is fully initialized
            keyFile = (File) data.getSerializableExtra("chosenFile");
        }
    }

    /**
     * Stars the {@link KeyChoiceActivity} to let the user choose an key file.
     */
    protected void startKeyChooserForResult() {
        log.debug("Starting key chooser");
        Intent intent = new Intent(this, KeyChoiceActivity.class);
        startActivityForResult(intent, RESULT_KEY_CHOICE);
    }

    /**
     * Stars the {@link StreamViewerActivity} to process the stream after 
     * the key file was chosen.
     * 
     * @param addr The socket address.
     * @param keyFile The key file.
     */
    protected void startStreamViewer(InetSocketAddress addr, File keyFile) {
        log.debug("Starting stream viewer with: connectAddr={} keyFile={}", addr, keyFile);
        Intent intent = new Intent(this, StreamViewerActivity.class);
        intent.putExtra("connectAddr", addr);
        intent.putExtra("keyFile", keyFile);
        startActivity(intent);
    }
}