package cryptocast.client.fileChooser;

/**
 * This class allows you to browse the files and folders of the SD card an choose one file.
 */
public abstract class FileChooser extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        
    }
    
    private void fill(File f) {
        
    }
    
    private abstract void onFileClick(ListElement o);

}
