package cryptocast.client;

import android.os.Bundle;
import android.view.Menu;

/**
 * This activity contains the abouts.
 */
public class AboutActivity extends ClientActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_about);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_about, menu);
        return true;
    }
}
