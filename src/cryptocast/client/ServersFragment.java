package cryptocast.client;

import java.net.InetSocketAddress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class is used to pop up an error message.
 */
public class ServersFragment extends MessageFragment {
    
    private ListView listing;
    
    

    public ServersFragment(final MainActivity mainActivity, String message, final ListView listing) {
        super(message);
        this.listing = listing;
        listing.setClickable(true);
        listing.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                InetSocketAddress address = (InetSocketAddress) listing.getItemAtPosition(position);
                mainActivity.getEditHostname().setText(address.getHostName());
                mainActivity.getEditPort().setText(String.valueOf(address.getPort()));
             
             }
            
         });        
    }
    
    @Override
    public Dialog onCreateDialog(Bundle b) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setPositiveButton(R.string.ok, clickHandler);
        builder.setView(listing);
        // Create the AlertDialog object and return it
        return builder.create();
    }
   

    
}
