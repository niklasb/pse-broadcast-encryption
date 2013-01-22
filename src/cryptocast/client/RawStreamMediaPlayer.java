package cryptocast.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.media.AudioManager;
import android.media.MediaPlayer;

import cryptocast.comm.SimpleHttpStreamServer;

/**
 * Media player to play audio from a raw {@link InputStream}
 */
public class RawStreamMediaPlayer implements MediaPlayer.OnCompletionListener,
                                             MediaPlayer.OnErrorListener,
                                             MediaPlayer.OnBufferingUpdateListener,
                                             MediaPlayer.OnInfoListener, 
                                             MediaPlayer.OnPreparedListener,
                                             SimpleHttpStreamServer.OnErrorListener {
	/**
	 * Interface for completion callback.
	 */
    public static interface OnCompletionListener {
    	/**
    	 * Called after completion.
    	 * 
    	 * @param p The media player.
    	 */
        public void onCompletion(RawStreamMediaPlayer p);
    }
    
    /**
     * Interface for error callback.
     */
    public static interface OnErrorListener {
    	/**
    	 * Called if any error occurs in the media player.
    	 * 
    	 * @param p The media player.
    	 * @param what The error code.
    	 * @param extra Extra info
    	 * @return true, if the error was handled, false otherwise
    	 */
        public boolean onError(RawStreamMediaPlayer p, int what, int extra);
        
        /**
         * Called if any error occurs in the HTTP server.
         * 
         * @param p The media player.
         * @param e The exception
         * @return true, if the error was handled, false otherwise
         */
        public boolean onError(RawStreamMediaPlayer p, Exception e);
    }
    
    /**
     * Interface for on prepared callback.
     */
    public static interface OnPreparedListener {
        /**
         * Called if player is prepared.
         * 
         * @param p The media player.
         */
        public void onPrepared(RawStreamMediaPlayer p);
    }
    
    /**
     * Interface for on info callback.
     */
    public static interface OnInfoListener {
        /**
         * Called if player provides new info.
         * 
         * @param p The media player.
         */
        public void onInfo(RawStreamMediaPlayer p, int what, int extra);
    }
    
    private static final Logger log = LoggerFactory
            .getLogger(RawStreamMediaPlayer.class);
    
    private MediaPlayer player = new MediaPlayer();
    private SimpleHttpStreamServer server;
    private InputStream in;
    private Thread worker;
    private String contentType;
    private OnCompletionListener completionListener;
    private OnErrorListener errorListener;
    private OnPreparedListener preparedListener;
    private OnInfoListener infoListener;

    
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
     * Set the info listener object.
     * @param preparedListener prepared listener object to set.
     */
    public void setOnInfoListener(OnInfoListener infoListener) {
        this.infoListener = infoListener;
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
                   0x400000,
                   this);
        worker = new Thread(server);
        worker.start();
        int port = server.waitForListener();
        log.debug("Temporary HTTP server bound to Port " + port);
        player.setLooping(false);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDataSource("http://127.0.0.1:" + port + "/");
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnInfoListener(this);
        player.setOnPreparedListener(this);
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
        log.trace("onBufferingUpdate: percent={}", percent);
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        log.debug("Player prepared.");
        if (preparedListener != null)
            preparedListener.onPrepared(this);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        log.debug("onInfo: {} {}", formatInfo(what), extra);
        if (infoListener != null) {
            infoListener.onInfo(this, what, extra);
        }
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
     * @return whether the player is playing
     */
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public boolean onError(Exception e) {
        if (this.errorListener == null)
            return false;
        return this.errorListener.onError(this, e);
    }
}
