package cryptocast.client;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This activity is responsible for decrypting the received data
 * and viewing it.
 */
public class StreamViewerActivity extends ClientActivity
                                  implements RawStreamMediaPlayer.OnCompletionListener,
                                             RawStreamMediaPlayer.OnErrorListener,
                                             MediaController.MediaPlayerControl,
                                             OnTouchListener,
                                             RawStreamMediaPlayer.OnPreparedListener,
                                             RawStreamMediaPlayer.OnInfoListener {
    
    private static final Logger log = LoggerFactory
            .getLogger(StreamViewerActivity.class);
    
    private RawStreamMediaPlayer player = new RawStreamMediaPlayer();
    private MediaController mediaController;
    private InetSocketAddress connectAddr;
    private File keyFile;
    private Socket sock;
    private ProgressBar spinner;
    private TextView statusText;
    private boolean finished = false;
    
    private StreamConnector connector;
    
    private static final String READY_TO_PLAY = "Ready to play." +
    System.getProperty("line.separator") + "(Touch to show controls)";

    
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_stream_viewer);
        // prevent landscape orientation
        this.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle args = getIntent().getExtras();
        connectAddr = (InetSocketAddress) args.getSerializable("connectAddr");
        keyFile = (File) args.getSerializable("keyFile");
        log.debug("Created with: connectAddr={} keyFile={}", connectAddr, keyFile);
        
        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.MediaController1));
        mediaController.setEnabled(false);
        
        findViewById(R.id.MediaController1).setOnTouchListener(this);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        
        statusText = (TextView) findViewById(R.id.textView1);
        statusText.setGravity(Gravity.CENTER_HORIZONTAL);
        
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
        player.setOnInfoListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        connector = new StreamConnector(sock, connectAddr, keyFile, player, this);
        new Thread(connector).start();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        player.start();
    }
    
    @Override
    protected void onPause() {
        cleanup();
        super.onPause();
    }

    private void cleanup() {
        if (finished)
            return;
        try {
            player.stop();
            connector.stop();
        } catch (Throwable e) {
            log.warn("Exception during cleanup:", e);
        }
        finished = true;
    }
    
    @Override
    public void onCompletion(RawStreamMediaPlayer p) {
        connector.stop();
    }
    
    @Override
    public boolean onError(RawStreamMediaPlayer p, int what, int extra) {
        if (finished) 
            return false;
        log.error("MediaPlayer error: {} {}", formatError(what), formatError(extra));
        handleError();
        return false;
    }

    @Override
    public boolean onError(RawStreamMediaPlayer p, Exception e) {
        if (finished) 
            return false;
        log.error("HTTP server error:", e);
        handleError();
        return false;
    }
    
    protected void handleError(String msg) {
        app.getServerHistory().invalidateKeyFile(connectAddr);
        cleanup();
        showErrorDialog(msg, finishOnClick);
    }
    
    protected void handleError() {
        handleError("Error while playing stream! Please try to reconnect "
                  + "and check if you selected the correct key file.");
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

    /**
     * Toggle playback play/pause. Will pause if in play mode and continue if
     * in pause mode.
     */
    public void togglePlay() { }


    @Override
    public boolean isPlaying() { 
        return player.isPlaying();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public void pause() {
        log.debug("Pause called.");
        player.pause();
    }

    @Override
    public void seekTo(int pos) {
    }

    @Override
    public void start() {
        log.debug("Start called.");
        player.start();
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        mediaController.show();
        return false;
    }

    @Override
    public void onPrepared(RawStreamMediaPlayer arg0) {
        spinner.setVisibility(View.INVISIBLE);
        mediaController.setEnabled(true);
        setStatusText(READY_TO_PLAY);
        player.start();
        mediaController.show();
    }
    
    /**
     * Creates a error dialog and finishes the activity on click.
     * @param message the dialog message
     */
    public void createErrorPopup(String message) {
        if (hasWindowFocus()) {
            showErrorDialog(message, finishOnClick);
        }
    }
    
    /**
     * Updates the status label text. Usable from other threads,
     * @param text the text
     */
    public void setStatusText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
            }
        });
    }

    @Override
    public void onInfo(RawStreamMediaPlayer p, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) { // needs to buffer
            spinner.setVisibility(View.VISIBLE);
            setStatusText("Buffering...");
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) { // continues playback
            spinner.setVisibility(View.INVISIBLE);
            setStatusText(READY_TO_PLAY);
        }
    }
}
