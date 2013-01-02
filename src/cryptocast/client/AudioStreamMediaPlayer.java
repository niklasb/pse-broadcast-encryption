package cryptocast.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.media.AudioManager;
import android.media.MediaPlayer;

import cryptocast.comm.SimpleHttpStreamServer;

public class AudioStreamMediaPlayer implements MediaPlayer.OnCompletionListener,
                                               MediaPlayer.OnErrorListener,
                                               MediaPlayer.OnBufferingUpdateListener,
                                               MediaPlayer.OnInfoListener {
    public static interface OnCompletionListener {
        public void onCompletion(AudioStreamMediaPlayer p);
    }
    
    public static interface OnErrorListener {
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

    public void setRawDataSource(InputStream in, String contentType) throws IOException {
        this.in = in;
        this.contentType = contentType;
    }

    public void setOnCompletionListener(OnCompletionListener completionListener) {
        this.completionListener = completionListener;
    }
    
    public void setOnErrorListener(OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }
    
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
        player.prepare();
    }

    public void start() {
        player.start();
    }

    public void stop() {
        player.stop();
    }
    
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
}
