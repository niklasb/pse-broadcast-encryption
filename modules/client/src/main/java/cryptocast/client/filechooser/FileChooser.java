package cryptocast.client.filechooser;

import java.io.File;
import cryptocast.client.R;

import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A file chooser that is used to choose a key file from the filesystem.
 */
public class FileChooser implements OnItemClickListener {
    private String filePattern;
    private FileChooserState state;
    private Activity act;
    private TextView currentDir;
    private ListView listing;
    
    /**
     * Initializes a file with the given file pattern.
     * 
     * @param filePattern The pattern of the file.
     */
    public FileChooser(String filePattern) {
        this.filePattern = filePattern;
        this.state = FileChooserState.fromDirectory(
                Environment.getExternalStorageDirectory());
    }
    
    /**
     * Returns the pattern of the file.
     * 
     * @return The pattern of the file.
     */
    public String getFilePattern() {
        return filePattern;
    }

    private FileChooserState getStateFromDir(File dir) {
        return FileChooserState.fromDirectory(dir, filePattern);
    }
    
    /**
     * initializes the file chooser for the given activity.
     * 
     * @param act The activity.
     */
    public void init(Activity act) {
        this.act = act;
        act.setContentView(R.layout.activity_file_chooser);
        currentDir = (TextView) act.findViewById(R.id.textView1);
        listing = (ListView) act.findViewById(R.id.listView1);
        listing.setOnItemClickListener(this);
        updateState(getStateFromDir(Environment.getExternalStorageDirectory()));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        ListElement item = state.getItems().get(pos);
        if (item instanceof FileListElement) {
            handleFileClick(item.getPath());
        } else {
            handleDirClick(item.getPath());
        }
    }

    private void handleFileClick(File file) {
        Intent result = new Intent();
        result.putExtra("chosenFile", file);
        act.setResult(Activity.RESULT_OK, result);
        act.finish();
    }

    private void handleDirClick(File dir) {
        updateState(getStateFromDir(dir));
    }
    
    private void updateState(FileChooserState state) {
        this.state = state;
        
        currentDir.setText("Current Dir: " + state.getCurrentDir().getAbsolutePath());
        listing.setAdapter(new FileArrayAdapter(act, act.getResources(), state.getItems()));
    }
}