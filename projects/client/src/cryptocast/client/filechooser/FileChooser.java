package cryptocast.client.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cryptocast.client.HelpActivity;
import cryptocast.client.MainActivity;
import cryptocast.client.OptionsActivity;
import cryptocast.client.R;
import cryptocast.client.StreamViewerActivity;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileChooser extends Activity implements OnItemClickListener {
    private FileChooserState state;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        updateState(FileChooserState.fromDirectory(Environment.getExternalStorageDirectory()));
        
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(this);
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
        // TODO
    }
    
    private void handleDirClick(File dir) {
        updateState(FileChooserState.fromDirectory(dir));
    }
    
    private void updateState(FileChooserState state) {
        this.state = state;
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText("Current Dir: " + state.getCurrentDir().getAbsolutePath());
        
        ListView listView = (ListView) findViewById(R.id.listView1);
        ArrayAdapter<ListElement> adapter = new ArrayAdapter<ListElement>(this, android.R.layout.simple_list_item_1, 
                android.R.id.text1, state.getItems());
        listView.setAdapter(adapter);
    }
}