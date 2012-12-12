package cryptocast.client;

import android.support.v4.app.FragmentActivity;
import cryptocast.comm.InChannel;

/**
 * This activity is responsible for decrypting the received data
 * and viewing it.
 */
public class StreamViewerActivity extends FragmentActivity {
    /**
     * Initializes a viewer.
     * @param inputStream The data stream
     */
    public StreamViewerActivity(InChannel inputStream) { }

    /** Handles a click on the bottom menu.
     * @param item The clicked menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
