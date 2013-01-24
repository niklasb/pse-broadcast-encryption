package cryptocast.client;

import java.io.File;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Optional;

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
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        editHostname.setText(app.getHostnameInput());
        editPort.setText(app.getPortInput());

        if (keyFile != null) {
            //TODO is this still needed? it is not executed when starting a stream!
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
        if (!haveNetwork()) {
            showErrorDialog("Not connected to a network!");
            return;
        }
        if (mAddr.get().isUnresolved()) {
            showErrorDialog("Could not resolve hostname!");
            return;
        }
        if (app.getWifiOnlyOption() && !haveWiFi()) {
            log.debug("Connecting impossible: WiFi-Only mode on and not connected via Wifi.");
            showErrorDialog("Will not connect without WiFi, please check your options!");
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
     * Starts a new Server Fragment, showing the list of all servers and a cancel Button.
     * @param view The view from which this method was called.
     */
    public void onOldServers(View view) {
        ServersFragment frag = new ServersFragment(app, "Please choose a server.", 
            new ServersFragment.OnServerSelected() {
                @Override
                public void onServerSelected(InetSocketAddress addr) {
                    editHostname.setText(addr.getHostName());
                    editPort.setText("" + addr.getPort());
                }
            });
        frag.show(getSupportFragmentManager(), null);
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
    
    /**
     * Checks whether the hostname is correct.
     * 
     * @param hostname The hostname.
     * @return <code>true</code>, if the hostname is correct; <code>false</code> otherwise.
     */
    protected boolean checkHostname(String hostname) {
        // TODO any other criterion?
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
     * Starts the {@link StreamViewerActivity} to process the stream after 
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

    private NetworkInfo getNetworkInfo() {
        return ((ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }
    
    private boolean haveNetwork() {
        NetworkInfo ni = getNetworkInfo();
        return ni != null && ni.isConnected();
    }
    
    private boolean haveWiFi() {
        log.debug("checking if connected via WiFi");
        NetworkInfo ni = getNetworkInfo();
        return ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Returns the hostname textfield.
     * @return the host textfield
     */
    public TextView getEditHostname() {
        return editHostname;
    }

    /**
     * Returns the port textfield.
     * @return the port textfield
     */
    public TextView getEditPort() {
        return editPort;
    }

}