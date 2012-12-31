package cryptocast.client;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public class ClientActivity extends FragmentActivity {
    protected ClientApplication app;
    protected DialogInterface.OnClickListener finishOnClick;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        app = ((ClientApplication) getApplication());
        final Activity that = this;
        finishOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                that.finish();
            }
        };
    }
    
    @Override
    protected void onPause() {
        app.saveState();
        super.onPause();
    }
    
    protected void showErrorDialog(String msg) {
        MessageFragment frag = new MessageFragment(msg);
        frag.show(getSupportFragmentManager(), null);
    }
    
    protected void showErrorDialog(String msg, 
            DialogInterface.OnClickListener clickHandler) {
        MessageFragment frag = new MessageFragment(msg, clickHandler);
        frag.show(getSupportFragmentManager(), null);
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
}
