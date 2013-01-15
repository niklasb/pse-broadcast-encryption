package cryptocast.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cryptocast.client.bufferedmediaplayer.BufferedMediaPlayer;
import cryptocast.client.bufferedmediaplayer.OnStatusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MediaPlayer extends Activity implements OnStatusChangeListener {

    private Button play;
    private Button pause;
    private Button stop;
    private static TextView status;

    private BufferedMediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        init();
        try {
            player.setDataSource(new FileInputStream(new File("/mnt/sdcard/whistle_long.mp3")));
            player.prepare();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_media_player, menu);
        return true;
    }
    
    private void init() {
        play = (Button) findViewById(R.id.button1);
        pause = (Button) findViewById(R.id.button2);
        stop = (Button) findViewById(R.id.Button01);
        status = (TextView) findViewById(R.id.textView1);
        
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
                setPlayingStatus(true);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
                setPlayingStatus(false);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                setPlayingStatus(false);
            }
        });
        status.setText("");
        player = new BufferedMediaPlayer();
        player.addOnStatusChangeListener(this);
    }
    
    @Override
    public void onStatusChange(String message) {
        updateLabel(message);
    }
    
    private void updateLabel(String message) {
        Message m = new Message();
        m.obj = message;
        textChangeHandler.sendMessage(m);
    }
    
    private void setPlayingStatus(boolean playing) {
        play.setEnabled(!playing);
        pause.setEnabled(playing);
    }
    

    private static Handler textChangeHandler  = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String text = (String)msg.obj;
            status.setText(text);
        }
    };
}
