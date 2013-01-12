package cryptocast.client;

import cryptocast.client.bufferedmediaplayer.BufferedMediaPlayer;
import cryptocast.client.bufferedmediaplayer.OnStatusChangeListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MediaPlayer extends Activity implements OnStatusChangeListener {

    private Button play;
    private Button pause;
    private Button stop;
    private TextView status;

    private BufferedMediaPlayer player;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        
        init();
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
        status = (Button) findViewById(R.id.textView2);
        
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
            }
        });
        
        player = new BufferedMediaPlayer();
        player.addOnStatusChangeListener(this);
    }

    @Override
    public void onStatusChange(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void bufferUpdate(int percentage) {
        // TODO Auto-generated method stub
        
    }
}
