package cryptocast.client.fileChooser;

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
