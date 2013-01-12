package cryptocast.client.bufferedmediaplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

/**
 * This class represents a buffered media player.
 * @author Christoph
 *
 */
public class BufferedMediaPlayer extends MediaPlayer implements OnCompletionListener, 
BufferedFileListener, Runnable, OnPreparedListener {
    
    private static int MAXIMUM_BUFFERED_FILES = 8;
    private static String BUFFER_LOCATION = "/mnt/sdcard/mpbuffer/";
    public static String BUFFER_NAME = "streambuffer";
    public static int AMOUNT_BUFFERED_PLAYERS = 2; // needs to be > 0
    
    private Queue<MediaPlayer> idlePlayers;
    private Queue<MediaPlayer> preparingPlayers;
    private Queue<MediaPlayer> preparedPlayers;

    private MediaPlayer playingPlayer;
    
    private BlockingQueue<File> bufferedFiles;
    private BlockingQueue<File> emptyBufferFiles;
    private HashMap<MediaPlayer, File> playedFiles;
    
    private InputStream stream;
    private StreamSaver saver;
    private boolean shouldBePlaying;
    private LinkedList<OnStatusChangeListener> statusListeners;
    
    /**
     * Creates a new BufferedMediaPlayer.
     */
    public BufferedMediaPlayer() {
        bufferedFiles = new LinkedBlockingQueue<File>();
        idlePlayers = new LinkedList<MediaPlayer>();
        preparingPlayers = new LinkedList<MediaPlayer>();
        preparedPlayers = new LinkedList<MediaPlayer>();
        playedFiles = new HashMap<MediaPlayer, File>();
        statusListeners = new LinkedList<OnStatusChangeListener>();
        
        for (int i = 0; i < AMOUNT_BUFFERED_PLAYERS; i++) {
           MediaPlayer player = new MediaPlayer();
           player.setOnPreparedListener(this);
           idlePlayers.add(player);
        }
        /* If there are buffer files left, remove them.
         * (May occur if the program crashed and the files weren't cleaned up properly. 
         */
        removeBufferFiles();
        emptyBufferFiles = createBufferFiles();
        shouldBePlaying = false;
    }
    
    /**
     * Sets the stream which will be played.
     * @param stream the stream to play
     */
    public void setDataSource(InputStream stream) {
        this.stream = stream;
    }
    
    /**
     * Starts playing the stream files.
     */
    public void start() {
        if (stream == null) {
            return;
        }
        if (saver == null) {
            prepare();
        } else if (!saver.isStreaming()) {
            saver.startStreaming();
        }
        shouldBePlaying = true;
    }
    
    /**
     * Stops the player and stops the stream.
     */
    public void stop() {
        pause();
        saver.stopStreaming();
    }
    /**
     * Pauses the player but keeps streaming and buffering.
     */
    public void pause() {
        shouldBePlaying = false;
        if (playingPlayer != null) {
            playingPlayer.stop();
        }
    }
    
    /**
     * Prepares the player and starts streaming.
     */
    public void prepare() {
        saver = new StreamSaver(stream, emptyBufferFiles);
        saver.addBufferedFileListener(this);
        new Thread(saver).start();
        new Thread(this).start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (playingPlayer != null) {
            playingPlayer.start();
        } else {
            playNextFile();
        }
    }
    
    public void addOnStatusChangeListener(OnStatusChangeListener listener) {
        statusListeners.add(listener);
    }
    
    @Override
    public void addBufferedFile(File file) {
        //Log.d(this.getClass().getName(), "Buffered File added " + file.toString());
        bufferedFiles.add(file);
    }

    @Override
    public void run() {
        while(true) {
            if (bufferedFiles.size() > 0 && idlePlayers.size() > 0) {
                MediaPlayer player = idlePlayers.remove();
                File file = bufferedFiles.remove();
                try {
                    player.setDataSource(file.toString());
                    player.prepare();
                    playedFiles.put(player, file);
                    preparingPlayers.add(player);
                    //Log.d(this.getClass().getName(), "Preparing next player");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (shouldBePlaying) {
                if (playingPlayer == null) {
                    playNextFile();
                } else if (!playingPlayer.isPlaying()) {
                    playNextFile();
                }
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Log.d(this.getClass().getName(), "Player succesfully prepared, ready to play.");
        preparedPlayers.add(mp);
        preparingPlayers.remove(mp);
    }

    private LinkedBlockingQueue<File> createBufferFiles() {
        // create buffer folder
        File bufferFolder = new File(BUFFER_LOCATION);
        if (!bufferFolder.exists()) {
            bufferFolder.mkdirs();
        }
        // create buffer files
        LinkedBlockingQueue<File> bufferFiles = new LinkedBlockingQueue<File>();
        File buffer = null;
        try {
            for (int i = 0; i < MAXIMUM_BUFFERED_FILES; i++) {
                buffer = File.createTempFile(BufferedMediaPlayer.BUFFER_NAME + i, ".mp3", 
                    new File(BUFFER_LOCATION));
                buffer.deleteOnExit();
                bufferFiles.add(buffer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferFiles;
    }
    
    private void removeBufferFiles() {
        File bufferFolder = new File(BUFFER_LOCATION);
        if (bufferFolder.exists()) {
            File[] files = bufferFolder.listFiles();
            for(int i=0; i<files.length; i++) {
                 files[i].delete();
            }
            bufferFolder.delete();
        }
    }

    private void playNextFile() {
        MediaPlayer nextPlayer = null;
        if (!preparedPlayers.isEmpty()) {
            nextPlayer = preparedPlayers.remove();
            nextPlayer.start();
        }
        
        if (playingPlayer != null) { // check for null for initial method call
            cleanupPlayer(playingPlayer);
        }
        playingPlayer = nextPlayer;
    }
    
    private void cleanupPlayer(MediaPlayer player) {
        File lastFile = playedFiles.get(player);
        playedFiles.remove(player);
        if (lastFile != null) {
            emptyBufferFiles.add(lastFile);
        }
        player.reset();
        idlePlayers.add(player);
    }

    @Override
    public void updateBufferProgress(int percentage) {
        for (OnStatusChangeListener listener : statusListeners) {
            listener.bufferUpdate(percentage);
        }
        
    }
}
