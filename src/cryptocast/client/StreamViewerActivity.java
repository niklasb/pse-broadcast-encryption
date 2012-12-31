package cryptocast.client;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

/**
 * This activity is responsible for decrypting the received data
 * and viewing it.
 */
public class StreamViewerActivity extends ClientActivity {
    private static final Logger log = LoggerFactory
            .getLogger(StreamViewerActivity.class);
    
    private AudioStreamMediaPlayer player = new AudioStreamMediaPlayer();
    private InputStream in;
    private String hostname;
    private int port;
    private File keyFile;
    
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_stream_viewer);
        Bundle args = getIntent().getExtras();
        hostname = args.getString("hostname");
        keyFile = new File(args.getString("keyFile"));
        port = 21337;
        log.debug("Created with: hostname={} keyFile={} port={}", 
                new Object[] { hostname, keyFile, port });
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
        log.debug("Connecting to {}:{}", hostname, port);
        Socket sock;
        try {
            sock = new Socket(hostname, port);
        } catch (Exception e) {
            log.error("Could not connect to target server", e);
            showErrorDialog("Could not connect to server!", finishOnClick);
            return;
        }
        try {
            log.debug("Starting media player");
            try {
                player.setRawDataSource(sock.getInputStream(), "audio/mpeg");
                player.prepare();
            } catch (Exception e) {
                log.error("Error while playing stream", e);
                showErrorDialog("Error while playing stream!", finishOnClick);
                return;
            }
            player.start();
        } finally {
            try {
                // TODO close *somewhere*
                //sock.close();
            } catch (Throwable e) { }
        }
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
