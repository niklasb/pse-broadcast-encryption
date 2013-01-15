package cryptocast.client.bufferedmediaplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

/**
 * This class represents a buffered media player.
 * @author Christoph
 *
 */
public class BufferedMediaPlayer implements OnCompletionListener, 
BufferedFileListener, Runnable, OnPreparedListener {
    
    private static int MAXIMUM_BUFFERED_FILES = 8;
    private static String BUFFER_LOCATION = Environment.getExternalStorageDirectory()
            + "/mpbuffer/";
    public static String BUFFER_NAME = "streambuffer";
    public static int AMOUNT_BUFFERED_PLAYERS = 3; // needs to be > 0
    
    private Queue<MediaPlayer> idlePlayers;
    private Queue<MediaPlayer> preparingPlayers;
    private Queue<MediaPlayer> preparedPlayers;

    private MediaPlayer currentPlayer;
    
    private Queue<File> bufferedFiles;
    private Queue<File> emptyBufferFiles;
    private HashMap<MediaPlayer, File> associatedFiles;
    
    private StreamSaver saver;
    private boolean shouldBePlaying;
    private boolean stopped;
    private boolean running;
    private LinkedList<OnStatusChangeListener> statusListeners;
    private static String LINE_BREAK =  System.getProperty("line.separator");
    private static long BROADCAST_INTERVALL = 500L;
    private long lastBroadcast = 0L;
    
    /**
     * Creates a new BufferedMediaPlayer.
     */
    public BufferedMediaPlayer() {
        bufferedFiles = new LinkedBlockingQueue<File>();
        idlePlayers = new LinkedList<MediaPlayer>();
        preparingPlayers = new LinkedList<MediaPlayer>();
        preparedPlayers = new LinkedList<MediaPlayer>();
        associatedFiles = new HashMap<MediaPlayer, File>();
        statusListeners = new LinkedList<OnStatusChangeListener>();
        saver = new StreamSaver();
        
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
        stopped = false;
        running = true;
        currentPlayer = idlePlayers.remove();
    }
    
    /**
     * Sets the stream which will be played.
     * @param stream the stream to play
     */
    public void setDataSource(InputStream stream) {
        saver.setStream(stream, emptyBufferFiles);
    }
    
    /**
     * Starts playing the stream files.
     */
    public void start() {
        if (!saver.isStreaming()) {
            saver.startStreaming();
        }
        stopped = false;
        shouldBePlaying = true;
    }
    
    /**
     * Stops the player and stops the stream.
     */
    public void stop() {
        pause();
        stopped = true;
        saver.stopStreaming();
        while (!preparingPlayers.isEmpty()) {
            MediaPlayer player = preparingPlayers.remove();
            cleanupPlayer(player);
            idlePlayers.add(player);
        }
        while (!preparedPlayers.isEmpty()) {
            MediaPlayer player = preparedPlayers.remove();
            cleanupPlayer(player);
            idlePlayers.add(player);
        }
        while (!bufferedFiles.isEmpty()) {
            emptyBufferFiles.add(bufferedFiles.remove());
        }
        cleanupPlayer(currentPlayer);
        associatedFiles.clear();
        
        broadcastBufferStatus();
    }
    /**
     * Pauses the player but keeps streaming and buffering.
     */
    public void pause() {
        shouldBePlaying = false;
        if (currentPlayer.isPlaying()) {
            currentPlayer.stop();
        }
    }

    /**
     * @return is playing
     */
    public boolean isPlaying() {
        return currentPlayer.isPlaying();
    }
    
    /**
     * 
     * @return is streaming
     */
    public boolean isStreaming() {
        return saver.isStreaming();
    }
    
    /**
     * Adds the given listener.
     * @param listener listener to add
     */
    public void addOnStatusChangeListener(OnStatusChangeListener listener) {
        statusListeners.add(listener);
    }
    
    /**
     * Prepares the player and starts streaming.
     */
    public void prepare() {
        saver.addBufferedFileListener(this);
        saver.startStreaming();
        new Thread(saver).start();
        new Thread(this).start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (shouldBePlaying) {
            playNextFile();
        }
    }

    @Override
    public void addBufferedFile(File file) {
        //Log.d(this.getClass().getName(), "Buffered File added " + file.toString());
        bufferedFiles.add(file);
    }

    @Override
    public void run() {
        while(running) {
            if (!stopped) {
                tryToPrepareNextPlayer();
                if (shouldBePlaying && !currentPlayer.isPlaying()) {
                    playNextFile();
                }
            }
            
            long currentTime = SystemClock.currentThreadTimeMillis();
            if (currentTime - lastBroadcast > BROADCAST_INTERVALL) {
                broadcastBufferStatus();
                lastBroadcast = currentTime;
            }
        }
    }
    
    @Override
    public void onPrepared(MediaPlayer mp) {
        while (!preparingPlayers.remove(mp)) {
            Log.d(this.getClass().getName(), "ERROR AT REMOVING");
        }
        preparedPlayers.add(mp);
    }

    private void broadcastBufferStatus() {
        for (OnStatusChangeListener listener : statusListeners) {
            listener.onStatusChange(
                    "Files: "  + emptyBufferFiles.size() + " empty; " + bufferedFiles.size() 
                    + " buffered; " + associatedFiles.size() + " associated;" + LINE_BREAK
                    + "Players: " + idlePlayers.size() + " idle; " + preparingPlayers.size() 
                    + " preparing; " + preparedPlayers.size() + " prepared" + LINE_BREAK
                    + "Current bufferfile: " + saver.getBufferProgress() + "%");
        }
    }

    private void tryToPrepareNextPlayer() {
        if (bufferedFiles.size() > 0 && idlePlayers.size() > 0) {
            MediaPlayer player = idlePlayers.remove();
            File file = bufferedFiles.remove();
            try {
                player.setDataSource(file.toString());
                associatedFiles.put(player, file);
                preparingPlayers.add(player);
                player.prepare();
                //Log.d(this.getClass().getName(), "Preparing next player");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        if (!preparedPlayers.isEmpty()) {
            preparedPlayers.peek().start();
            MediaPlayer nextPlayer = preparedPlayers.remove();
            cleanupPlayer(currentPlayer);
            currentPlayer = nextPlayer;
        }
    }
    
    private void cleanupPlayer(MediaPlayer player) {
        File lastFile = associatedFiles.get(player);
        if (associatedFiles.containsKey(player)) {
            associatedFiles.remove(player);
            if (lastFile != null) {
                emptyBufferFiles.add(lastFile);
            }
            player.reset();
            idlePlayers.add(player);
        }
    }
}
