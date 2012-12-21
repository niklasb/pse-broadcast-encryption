package cryptocast.client.filechooser;

import java.io.File;
import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * An UI element that allows the user to browse the files and folders of the SD card and
 * choose one file.
 */
public abstract class FileChooser extends ListActivity {
    
    private File currentDir;
    
    
    /** Initialize the instance
     * @param savedInstanceState the stored state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File("/sdcard/");
        updateItems(currentDir);
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

    private void updateItems(File curDir) {
        this.setTitle("Directory: " + curDir.getName());
        File[] data =  curDir.listFiles();
        ArrayList<DirectoryListElement> folders = new ArrayList<DirectoryListElement>();
        ArrayList<FileListElement> files = new ArrayList<FileListElement>();
        
        for (File file : data) {
            if (file.isDirectory()) {
                folders.add(new DirectoryListElement(file));
            } else {
                files.add(new FileListElement(file));
            }
        }
    }
    
    /** Called when the user clicks a file in the list.
     * @param item The clicked list item.
     */
    protected abstract void onFileClick(FileListElement o);
}
