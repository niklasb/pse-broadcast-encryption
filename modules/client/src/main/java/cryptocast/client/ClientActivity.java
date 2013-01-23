package cryptocast.client;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Defines a client activity. Other activities extend this base activity, which provides basic
 * basic common functionality.
 */
public abstract class ClientActivity extends FragmentActivity {
	/**
     * The client application.
	 */
	protected ClientApplication app;
	/**
	 * Finish by click.
	 */
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
    
    /**
     * Shows a error message dialog. 
     * 
     * @param msg The error message.
     */
    protected void showErrorDialog(String msg) {
        MessageFragment frag = new MessageFragment(msg);
        frag.show(getSupportFragmentManager(), null);
    }
    
    /**
     * Shows a error message dialog by clicking a button.
     * 
     * @param msg The error message.
     * @param clickHandler The click handler.
     */
    protected void showErrorDialog(String msg, 
            DialogInterface.OnClickListener clickHandler) {
        MessageFragment frag = new MessageFragment(msg, clickHandler);
        frag.show(getSupportFragmentManager(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if (this instanceof HelpActivity) {
            menu.findItem(R.id.itemHelp).setEnabled(false);
        } else if (this instanceof OptionsActivity) {
            menu.findItem(R.id.itemOptions).setEnabled(false);
        } else if (this instanceof AboutActivity) {
            menu.findItem(R.id.itemAbout).setEnabled(false);
        }
        return true;
    }
    
    /** 
     * Handles a click on the main menu.
     * @param item The clicked item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle menu item click
        switch (item.getItemId()) {
        case R.id.itemOptions:
            intent = new Intent(this, OptionsActivity.class);
            startActivity(intent);
            return true;
        case R.id.itemHelp:
            intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            return true;
        case R.id.itemAbout:
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}