package cryptocast.client.bufferedmediaplayer;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;

import cryptocast.client.ClientTestRunner;

@RunWith(ClientTestRunner.class)
public class TestBufferedMediaPlayer implements OnStatusChangeListener {

    private int percentage = 0;
    
    @Test
    public void testMediaPlayer() {
        BufferedMediaPlayer player = new BufferedMediaPlayer();
        InputStream stream = mockStream();
        player.addOnStatusChangeListener(this);
        player.setDataSource(stream);
        player.prepare();
        
        assertFalse(player.isPlaying());
        player.start();
        /*while (percentage < 100) {
            // give the player time to buffer the first file
        }
        assertTrue(player.isPlaying());
        player.pause();
        assertFalse(player.isPlaying());
        assertTrue(player.isBuffering());
        player.stop();
        assertFalse(player.isBuffering());
        */

    }
    
    public InputStream mockStream() {
        ByteArrayInputStream stream = spy(new ByteArrayInputStream(new byte[1]));
        when(stream.available()).thenReturn(100000);
        when(stream.read()).thenReturn(1);
        return stream;
    }

    @Override
    public void onStatusChange(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void bufferUpdate(int percentage) {
        this.percentage = percentage;
    }
}


