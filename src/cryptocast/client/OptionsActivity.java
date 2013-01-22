package cryptocast.client;

import java.net.InetSocketAddress;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

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
        listing.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                    long id) {
                return true;
             }
            
         }); 
    }
    
    /** 
     * Inflates the option menu.
     * @param menu The menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_options, menu);
        return true;
    }
}
