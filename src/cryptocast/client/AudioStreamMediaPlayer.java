package cryptocast.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;

import cryptocast.comm.SimpleHttpStreamServer;
/**
 * Media player to playing a audio stream.
 */
public class AudioStreamMediaPlayer implements MediaPlayer.OnCompletionListener,
                                               MediaPlayer.OnErrorListener,
                                               MediaPlayer.OnBufferingUpdateListener,
                                               MediaPlayer.OnInfoListener {
	/** 
	 * Interface for completion callback.
	 */
    public static interface OnCompletionListener {
    	/**
    	 * Called after completion.
    	 * 
    	 * @param p The media player.
    	 */
        public void onCompletion(AudioStreamMediaPlayer p);
    }
    /**
     * Interface for error callback.
     */
    public static interface OnErrorListener {
    	/**
    	 * Called if any error occurs.
    	 * 
    	 * @param p The media player.
    	 * @param what The error code.
    	 * @param extra Extra info
    	 * @return <code>true</code>, if any error occurs or <code>false</code> otherwise. 
    	 */
        public boolean onError(AudioStreamMediaPlayer p, int what, int extra);
    }
    
    private static final Logger log = LoggerFactory
            .getLogger(AudioStreamMediaPlayer.class);
    
    private MediaPlayer player = new MediaPlayer();
    private SimpleHttpStreamServer server;
    private InputStream in;
    private Thread worker;
    private String contentType;
    private OnCompletionListener completionListener;
    private OnErrorListener errorListener;
    private OnPreparedListener preparedListener;

    
    /**
     * Sets the raw data.
     * 
     * @param in The input stream.
     * @param contentType The type of content. 
     * @throws IOException
     */
    public void setRawDataSource(InputStream in, String contentType) throws IOException {
        this.in = in;
        this.contentType = contentType;
    }

    /**
     * 
     * @param completionListener
     */
    public void setOnCompletionListener(OnCompletionListener completionListener) {
        this.completionListener = completionListener;
    }
    
    /**
     * Set the error listener object.
     * 
     * @param errorListener error listener object to set.
     */
    public void setOnErrorListener(OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }
    
    /**
     * Set the prepared listener object.
     * @param preparedListener prepared listener object to set.
     */
    public void setOnPreparedListener(OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }
    
    /**
     * Prepares the player with a data source and event listeners.
     * 
     * @throws IllegalStateException
     * @throws InterruptedException
     * @throws IOException
     */
    public void prepare() throws IllegalStateException, InterruptedException, IOException {
        if (in == null) {
            throw new IllegalStateException("setRawDataSource() has not been called");
        }
        server = new SimpleHttpStreamServer(
                   in, 
                   new InetSocketAddress("127.0.0.1", 0), 
                   contentType,
                   0x400000);
        worker = new Thread(server);
        worker.start();
        int port = server.waitForListener();
        log.debug("Temporary HTTP server bound to Port " + port);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDataSource("http://127.0.0.1:" + port + "/");
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnInfoListener(this);
        player.setOnPreparedListener(preparedListener);
        player.prepare();
    }

    /**
     * Starts the media player
     */
    public void start() {
        player.start();
    }

    /**
     * Stops the media player.
     */
    public void stop() {
        player.stop();
        player.release();
        if (worker != null) {
            worker.interrupt();
        }
    }
    
    /**
     * Pauses the media player.
     */
    public void pause() {
        player.pause();
    }
    
    @Override
    public boolean onError(MediaPlayer p, int what, int extra) {
        if (errorListener == null) {
            return false;
        }
        return errorListener.onError(this, what, extra);
    }
    
    @Override
    public void onCompletion(MediaPlayer p) {
        if (completionListener != null) {
            completionListener.onCompletion(this);
        }
        log.debug("Stopping HTTP server");
        worker.interrupt();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        log.debug("onBufferingUpdate: percent={}", percent);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        log.debug("onInfo: {} {}", formatInfo(what), extra);
        return false;
    }
    
    private String formatInfo(int what) {
        switch (what) {
        case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING: return "MEDIA_INFO_BAD_INTERLEAVING";
        case MediaPlayer.MEDIA_INFO_METADATA_UPDATE: return "MEDIA_INFO_METADATA_UPDATE";
        case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE: return "MEDIA_INFO_NOT_SEEKABLE";
        case MediaPlayer.MEDIA_INFO_UNKNOWN: return "MEDIA_INFO_UNKNOWN";
        case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING: return "MEDIA_INFO_VIDEO_TRACK_LAGGING";
        default: return "MediaInfo(" + what + ")";
        }
    }

    /**
     * Returns whether the player is playing.
     * @return whether the player is playing
     */
    public boolean isPlaying() {
        return player.isPlaying();
    }
}
