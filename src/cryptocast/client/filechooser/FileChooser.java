package cryptocast.client.fileChooser;

/**
 * An UI element that allows the user to browse the files and folders of the SD card and
 * choose one file.
 */
public abstract class FileChooser extends ListActivity {
    /** Initialize the instance
     * @param savedInstanceState the stored state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    /** Handles a click onto a list item.
     * @param lst The list
     * @param view The view
     * @param position The index of the list item
     * @param id The ID of the list item
     */
    @Override
    protected void onListItemClick(ListView lst, View view, int position, long id) {
    }

    private void fill(File f) {
    }
    private abstract void onFileClick(ListElement o);
}
