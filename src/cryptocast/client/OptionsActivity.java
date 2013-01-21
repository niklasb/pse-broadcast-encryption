package cryptocast.client;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

/** 
 * The option screen. 
 * 
 */
public class OptionsActivity extends ClientActivity {
    private static final Logger log = LoggerFactory
            .getLogger(ClientActivity.class);
    
    private ListView listing;
    private CheckBox wifiCheckBox;
    
    /** 
     * Receives the saved option state.
     * @param b the old state.
     */
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_options);
        wifiCheckBox = (CheckBox) findViewById(R.id.checkBoxWifi);
        wifiCheckBox.setChecked(app.getWifiOnlyOption());
        listing = (ListView) findViewById(R.id.listView1);
        listing.setAdapter(
                new ArrayAdapter<InetSocketAddress> (
                        app, R.layout.file_chooser_row, app.getServerHistory().getServerList()));

    }
    
    @Override
    protected void onResume() {
        super.onResume();
        wifiCheckBox.setChecked(app.getWifiOnlyOption());
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        app.setWifiOnlyOption(wifiCheckBox.isChecked());
    }
}
