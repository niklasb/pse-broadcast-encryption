package cryptocast.client;

import java.net.InetSocketAddress;
import java.util.List;

import cryptocast.client.R;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * An adapter between the ListElements and the view showing them.
 */
public class ServerListAdapter extends ArrayAdapter<InetSocketAddress> {
    private static final int RESOURCE = R.layout.server_list_row;
    private LayoutInflater inflater;
    private Resources res;
    
    /**
     * Constructs a new instance with the given attributes.
     * @param context The context in which this adapter is used.
     * @param textViewResourceId The view showing the data.
     * @param objects List with all elements which will be shown by the view.
     */
    public ServerListAdapter(Context context, Resources res, List<InetSocketAddress> elements) {
        super(context, RESOURCE, elements);
        this.inflater = LayoutInflater.from(context);
        this.res = res;
    }

    /**
     * @param position The position of the element in the list
     * @param convertView View which should be converted
     * @param parent The parent view group
     * @return A custom view of a list element at the given position in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InetSocketAddress item = getItem(position);
        View view = inflater.inflate(RESOURCE, null);
        TextView text = (TextView) view.findViewById(R.id.server_list_row_text);
        text.setText(item.getHostName() + ":" + item.getPort());
        return view;
    }
}
