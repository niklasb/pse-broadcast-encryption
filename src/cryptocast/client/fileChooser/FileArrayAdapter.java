package cryptocast.client.fileChooser;
/**
 * An adapter between the ListElements and the view showing them.
 */
public class FileArrayAdapter extends ArrayAdapter<ListElement> {
    
    /**
     * Constructs a new FileArrayAdapter with the given attributes.
     * @param context The context in which this adapter is used.
     * @param textViewResourceId The view showing the data.
     * @param objects List with all elements which will be shown by the view.
     */
    public FileArrayAdapter(Context context, int textViewResourceId, List<ListElement> objects) {
        
    }
    
    /**
     * Returns the ListElement at the given place in the list.
     * @param place the place were the item is in the list.
     * @return the ListElement defined by place.
     */
    public ListElement getItem(int place) {
        
    }
    
    /*
     * interface defined by API, don't know what it exactly does right now.
     * Returns a custom view of the ListElement at the given position in the list.
     * @param position Position of the ListElement in the list.
     * @param convertView View which should be converted.
     * @param parent 
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
    }
}
