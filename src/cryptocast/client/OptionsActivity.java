package cryptocast.client;

import java.net.InetSocketAddress;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** 
 * The option screen. 
 * 
 */
public class OptionsActivity extends ClientActivity {
    
    private ListView listing;

    /** 
     * Receives the saved option state.
     * @param b the old state.
     */
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_options);
        listing = (ListView) findViewById(R.id.listView1);
        listing.setAdapter(
                new ArrayAdapter<InetSocketAddress> (
                        app, R.layout.file_chooser_row, app.getServerHistory().getServerList()));

    }
}
