package cryptocast.client;

import android.os.Bundle;
import android.view.Menu;

public class HelpActivity extends ClientActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_help);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_help, menu);
        return true;
    }
}
