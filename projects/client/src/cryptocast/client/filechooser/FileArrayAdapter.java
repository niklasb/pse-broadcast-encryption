package cryptocast.client.filechooser;

import java.util.List;

import cryptocast.client.R;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * An adapter between the ListElements and the view showing them.
 */
public class FileArrayAdapter extends ArrayAdapter<ListElement> {
    
    private Context context;
    private int textViewResourceId;
    private List<ListElement> elements;
    
    
    /**
     * Constructs a new instance with the given attributes.
     * @param context The context in which this adapter is used.
     * @param textViewResourceId The view showing the data.
     * @param objects List with all elements which will be shown by the view.
     */
    public FileArrayAdapter(Context context, int textViewResourceId, List<ListElement> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.elements = objects;
    }

    /**
     * @param position The position of the item in the list.
     * @return The list element at the given position in the list.
     */
    public ListElement getItem(int position) {
        return elements.get(position);
    }

    /**
     * @param position The position of the element in the list
     * @param convertView View which should be converted
     * @param parent The parent view group
     * @return A custom view of a list element at the given position in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = 
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(textViewResourceId, null);
        }
        
        ListElement elem = elements.get(position);
        if (elem != null) {
            TextView t1 = (TextView) convertView.findViewById(R.id.textView1);
            TextView t2 = (TextView) convertView.findViewById(R.id.textView2);
            
            if(t1!=null)
             t1.setText(elem.getPath().getName());
            if(t2!=null)
             t2.setText(elem.getPath().getName());
    }
        
        return parent;
    }
}
