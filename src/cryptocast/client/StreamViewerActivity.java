package cryptocast.client;

import android.support.v4.app.FragmentActivity;
import cryptocast.comm.InChannel;

/**
 * This activity is responsible for decrypting the received data
 * stream and viewing it.
 */
public class StreamViewerActivity extends FragmentActivity {
    /**
     * Initializes a viewer
     * @param inputStream the data stream
     */
    public StreamViewerActivity(InChannel inputStream) {}

    /**
     * Toggle playback play/pause. Will pause if in play mode and continue if
     * in pause mode.
     */
    public void togglePlay() { }

    /**
     * @return whether the player is in playing mode
     */
    public boolean isPlaying() { return false; }
}
