package cryptocast.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class AudioStreamMediaPlayer implements OnCompletionListener {
    private MediaPlayer player = new MediaPlayer();
    private SimpleHttpStreamServer server;
    private InputStream in;
    private Thread worker;
    private String contentType;

    public void setRawDataSource(InputStream in, String contentType) throws IOException {
        this.in = in;
        this.contentType = contentType;
    }

    public void prepare() throws IllegalStateException, IOException {
        if (in == null) {
            throw new IllegalStateException("setRawDataSource() has not been called");
        }
        server = new SimpleHttpStreamServer(
                   in, 
                   new InetSocketAddress("127.0.0.1", 11337), 
                   contentType,
                   0x400000);
        worker = new Thread(server);
        worker.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) { }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDataSource("http://127.0.0.1:11337/");
        player.setOnCompletionListener(this);
        player.prepare();
    }

    public void start() {
        player.start();
    }

    @Override
    public void onCompletion(MediaPlayer p) {
        worker.interrupt();
    }
}
