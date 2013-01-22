package cryptocast.client;

import java.net.InetSocketAddress;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

/** 
 * The option screen. 
 * 
 */
public class OptionsActivity extends ClientActivity {
    private ListView listing;
    private CheckBox wifiCheckBox;
    private final String[] menuItems = {"Delete"};

    
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
        listing.setAdapter(new ServerListAdapter(
                   app, app.getServerHistory().getServerList()));
        registerForContextMenu(listing);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      if (v.getId()==R.id.listView1) {
        menu.setHeaderTitle("Options");
        for (int i = 0; i<menuItems.length; i++) {
          menu.add(Menu.NONE, i, i, menuItems[i]);
        }
      }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      int menuItemIndex = item.getItemId();
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      InetSocketAddress addr = (InetSocketAddress) listing.getItemAtPosition(info.position);
      switch(menuItemIndex) {
      case 0:
          app.getServerHistory().deleteServer(addr);
          finish();
          startActivity(getIntent());
      }
      return true;
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
