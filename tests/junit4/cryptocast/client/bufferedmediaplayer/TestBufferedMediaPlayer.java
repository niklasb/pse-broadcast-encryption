package cryptocast.client.bufferedmediaplayer;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import cryptocast.client.ClientTestRunner;

@RunWith(ClientTestRunner.class)
public class TestBufferedMediaPlayer implements OnStatusChangeListener {
    
    private final static int TIME_TO_PREPARE_IN_MS = 100;
    
    BufferedMediaPlayer player;
    InputStream stream;
            
    @Before
    public void testInit() {
        player = new BufferedMediaPlayer();
        stream = mockStream();
        player.addOnStatusChangeListener(this);
        player.setDataSource(stream);
        player.prepare();
        assertFalse(player.isPlaying());
        assertTrue(player.isStreaming());
    }
    
    @Test
    public void testStart() {
        player.start();
        try {
            Thread.sleep(TIME_TO_PREPARE_IN_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(player.isPlaying());
        assertTrue(player.isStreaming());
    }
    
    @Test
    public void testPause() {
        player.start();
        player.pause();
        assertFalse(player.isPlaying());
        assertTrue(player.isStreaming());
    }
    @Test
    public void testStop() {
        player.start();
        player.stop();
        assertFalse(player.isPlaying());
        assertFalse(player.isStreaming());
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
}


