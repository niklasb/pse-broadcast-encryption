package cryptocast.client;

import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * This activity is responsible for decrypting the received data
 * and viewing it.
 */
public class StreamViewerActivity extends ClientActivity
                                  implements AudioStreamMediaPlayer.OnCompletionListener,
                                             AudioStreamMediaPlayer.OnErrorListener {
    private static final Logger log = LoggerFactory
            .getLogger(StreamViewerActivity.class);
    
    private AudioStreamMediaPlayer player = new AudioStreamMediaPlayer();
    private InputStream in;
    private InetSocketAddress connectAddr;
    private File keyFile;
    private Socket sock;
    
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_stream_viewer);
        Bundle args = getIntent().getExtras();
        connectAddr = (InetSocketAddress) args.getSerializable("connectAddr");
        keyFile = (File) args.getSerializable("keyFile");
        log.debug("Created with: connectAddr={} keyFile={}", connectAddr, keyFile);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        InputStream in = getResources().openRawResource(R.raw.song);
//        AudioStreamMediaPlayer player = new AudioStreamMediaPlayer();
//        try {
//            player.setRawDataSource(in, "audio/mpeg");
//            player.prepare();
//            player.start();
//        } catch (Exception e) {
//            log.error("Could not play", e);
//        }
        log.debug("Connecting to {}", connectAddr);
        Socket sock = new Socket();
        try {
            sock.connect(connectAddr);
        } catch (Exception e) {
            log.error("Could not connect to target server", e);
            showErrorDialog("Could not connect to server!", finishOnClick);
            return;
        }
        try {
            log.debug("Starting media player");
            try {
                player.setRawDataSource(sock.getInputStream(), "audio/mpeg");
                player.setOnCompletionListener(this);
                player.setOnErrorListener(this);
                player.prepare();
            } catch (Exception e) {
                log.error("Error while playing stream", e);
                showErrorDialog("Error while playing stream!", finishOnClick);
                return;
            }
        } finally {
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        player.start();
    }
    
    @Override
    protected void onPause() {
        player.pause();
        super.onPause();
    }

    @Override
    public void onCompletion(AudioStreamMediaPlayer p) {
        try {
            sock.close();
        } catch (Throwable e) { }
    }
    
    @Override
    public boolean onError(AudioStreamMediaPlayer p, int what, int extra) {
        log.error("MediaPlayer error: {} {}", formatError(what), formatError(extra));
        return false;
    }
    
    private String formatError(int what) {
        switch (what) {
        case MediaPlayer.MEDIA_ERROR_UNKNOWN: return "MEDIA_ERROR_UNKNOWN";
        case MediaPlayer.MEDIA_ERROR_SERVER_DIED: return "MEDIA_ERROR_SERVER_DIED";
        case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK: 
            return "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK";
        default: return "MediaError(" + what + ")";
        }
    }

    @Override
    protected void onStop() {
        player.stop();
        super.onStop();
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
