package cryptocast.client;

import java.io.File;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class KeyChooser extends Activity implements OnItemClickListener {

    private File currentDir;
    private File[] currentDirData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_chooser);
        currentDir = new File("/sdcard/");
        updateItems(currentDir);
    }

    private void updateItems(File curDir) {
       File[] curDirFiles = curDir.listFiles();
        
       // insert 'up navigation' item TODO if not in sdcard directory
       currentDirData =  new File[curDirFiles.length + 1];
       currentDirData[0] = curDir.getParentFile();
       for (int i = 1; i < currentDirData.length; i++) {
          currentDirData[i] = curDirFiles[i - 1];
       }

        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText("Current Dir: " + currentDir.toString());

        
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(this);
        ArrayAdapter<File> adapter = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, 
                android.R.id.text1, currentDirData);
        
        listView.setAdapter(adapter);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        if (currentDirData[pos].isDirectory()) {
            currentDir = currentDirData[pos];
        }
        updateItems(currentDir);
        // TODO check if keyfile
    }
    


}
