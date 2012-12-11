package cryptocast.client.filechooser;

/**
 * An adapter between the ListElements and the view showing them.
 */
public class FileArrayAdapter extends ArrayAdapter<ListElement> {
    /**
     * Constructs a new instance with the given attributes.
     * @param context The context in which this adapter is used.
     * @param textViewResourceId The view showing the data.
     * @param objects List with all elements which will be shown by the view.
     */
    public FileArrayAdapter(Context context, int textViewResourceId, List<ListElement> objects) {
    }

    /**
     * @param position The position of the item in the list.
     * @return The list element at the given position in the list.
     */
    public ListElement getItem(int position) {
    }

    /**
     * @param position Position of the element in the list.
     * @param convertView View which should be converted.
     * @param parent The parent view group.
     * @return A custom view of a list element at the given position in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    }
}
