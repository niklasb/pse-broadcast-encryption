package cryptocast.client.filechooser;

import java.io.File;
import java.util.ArrayList;

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

    private File currentDir;
    private ArrayList<ListElement> currentDirList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        currentDir = Environment.getExternalStorageDirectory();
        currentDirList = new ArrayList<ListElement>();
        
        updateItems(currentDir);
        
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(this);
    }

    private void updateItems(File curDir) {
        File[] curDirFiles = curDir.listFiles();
        currentDirList.clear();

        currentDirList.add(new NavigateUpListElement(curDir.getParentFile()));
        for (File file : curDirFiles) {
            System.err.println(file + " isdir="+file.isDirectory() + " isfile="+file.isFile() + " listFiles="+file.listFiles());
            if (file.isDirectory()) {
                currentDirList.add(new DirectoryListElement(file));
            } else {
                currentDirList.add(new FileListElement(file));
            }
        }
        // TODO sort?
        
        // set title
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText("Current Dir: " + currentDir.toString());
        
        ListView listView = (ListView) findViewById(R.id.listView1);
        ArrayAdapter<ListElement> adapter = new ArrayAdapter<ListElement>(this, android.R.layout.simple_list_item_1, 
                android.R.id.text1, currentDirList);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /** Handles a click on the main menu.
     * @param item The clicked item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle menu item click
        switch (item.getItemId()) {
            case R.id.itemMain:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemOptions:
                intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemHelp:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.itemPlayer:
                intent = new Intent(this, StreamViewerActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        if (currentDirList.get(pos).getPath().isDirectory() 
                && currentDirList.get(pos).getPath().getParent() != null) {
            
            currentDir = currentDirList.get(pos).getPath();
            updateItems(currentDir);
        }
    }
}
