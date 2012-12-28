package cryptocast.client;

import java.io.InputStream;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

/**
 * This activity is responsible for decrypting the received data
 * and viewing it.
 */
public class StreamViewerActivity extends FragmentActivity {
    private AudioStreamMediaPlayer player;
    private InputStream in;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_viewer);
        // TODO initialize `in`
        player = new AudioStreamMediaPlayer();
        try {
            player.setDataSource(in);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        player.start();
    }
    
    /** Handles a click on the bottom menu.
     * @param item The clicked menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    /**
     * Toggle playback play/pause. Will pause if in play mode and continue if
     * in pause mode.
     */
    public void togglePlay() { }

    /**
     * @return Whether the player is in playing mode.
     */
    public boolean isPlaying() { return false; }
}
