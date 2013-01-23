package cryptocast.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * This class is used to pop up a list of all saved servers.
 */
public class ServersFragment extends DialogFragment {
    public static interface OnServerSelected {
        public void onServerSelected(InetSocketAddress addr);
    }
    
    private ListView listing;
    private AlertDialog dialog;
    private String message;
    private OnClickListener clickHandler;
    
    public ServersFragment(ClientApplication app, String message, final OnServerSelected callback) {
        super();
        this.message = message;
        List<InetSocketAddress> serverList = app.getServerHistory().getServerList();
        this.clickHandler = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        };
        if (serverList.size() == 0) {
            this.message = "No saved servers.";
        }
        listing = new ListView(app);
        listing.setAdapter(new ServerListAdapter(
                    app, serverList));
        listing.setClickable(true);
        listing.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                InetSocketAddress address = (InetSocketAddress) listing.getItemAtPosition(position);
                callback.onServerSelected(address);
                dialog.dismiss();                
             }
         });        
    }
    
    @Override
    public Dialog onCreateDialog(Bundle b) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setNegativeButton("Cancel", clickHandler);
        builder.setView(listing);
        builder.setCancelable(true);
        dialog = builder.create();
        // Create the AlertDialog object and return it
        return dialog;
    }
}
