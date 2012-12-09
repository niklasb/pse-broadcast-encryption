package cryptocast.client;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/** The option screen. */
public class OptionsActivity extends Activity {
    /** Receives the saved option state.
     * @param savedInstanceState the old state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }

    /** Inflates the option menu.
     * @param menu The menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_options, menu);
        return true;
    }
}
