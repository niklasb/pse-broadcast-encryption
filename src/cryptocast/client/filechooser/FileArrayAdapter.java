package cryptocast.client.filechooser;

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
public class FileArrayAdapter extends ArrayAdapter<ListElement> {
    private static final int RESOURCE = R.layout.file_chooser_row;
    private LayoutInflater inflater;
    private Resources res;
    
    /**
     * Constructs a new instance with the given attributes.
     * @param context The context in which this adapter is used.
     * @param textViewResourceId The view showing the data.
     * @param objects List with all elements which will be shown by the view.
     */
    public FileArrayAdapter(Context context, Resources res, List<ListElement> elements) {
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
        if (convertView != null) { 
            return convertView; 
        }
        convertView = inflater.inflate(RESOURCE, null);
        TextView text = (TextView) convertView.findViewById(R.id.file_row_text);
        text.setText(getItem(position).toString());
        text.setCompoundDrawablesWithIntrinsicBounds(getItem(position).getIcon(res), null, null, null);
        return convertView;
    }
}
