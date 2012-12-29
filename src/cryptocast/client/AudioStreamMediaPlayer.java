package cryptocast.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class AudioStreamMediaPlayer implements OnCompletionListener, OnErrorListener {
    private MediaPlayer player = new MediaPlayer();
    private SimpleHttpStreamServer server;
    private InputStream in;
    private Thread worker;

    public void setDataSource(InputStream in) throws IOException {
        this.in = in;
    }

    public void prepare() throws IllegalStateException, IOException {
        if (in == null) {
            throw new IllegalStateException("setDataSource() was not called");
        }
        server = new SimpleHttpStreamServer(in, 
                   new InetSocketAddress("127.0.0.1", 11337), "audio/mpeg");
        worker = new Thread(server);
        worker.start();
        
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDataSource("http://127.0.0.1:11337/");
        player.setOnCompletionListener(this);
    }

    public void start() {
        player.start();
    }

    @Override
    public void onCompletion(MediaPlayer p) {
        worker.interrupt();
    }

    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return false;
    }
}
