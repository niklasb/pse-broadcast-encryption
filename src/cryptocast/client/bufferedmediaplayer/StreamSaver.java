package cryptocast.client.bufferedmediaplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

/**
 * This class represents a stream saver.
 * @author Christoph
 *
 */
public class StreamSaver implements Runnable {
    
    private static int FILE_SIZE = 50000;
    
    private InputStream stream;
    private byte[] input;
    private Queue<File> usableBufferFiles;
    
    private ArrayList<BufferedFileListener> listeners;

    private boolean isStreaming;
    private boolean isRunning;
    
    /**
     * Creates a StreamSaver which streams from the given stream and uses the given files as buffer.
     * @param stream the stream
     * @param usableBufferFiles the buffer files
     */
    public StreamSaver(InputStream stream, Queue<File> usableBufferFiles) {
        this.stream = stream;
        this.usableBufferFiles = usableBufferFiles;
        isRunning = true;
        
        input = new byte[FILE_SIZE];
        listeners = new ArrayList<BufferedFileListener>();
    }

    @Override
    public void run() {
        while(isRunning) {
            while (isStreaming) {
                try {
                    if (!usableBufferFiles.isEmpty() && stream.available() > 0) {
                        stream.read(input);
                        File bufferFile = usableBufferFiles.remove();
                        FileOutputStream outStream = new FileOutputStream(bufferFile);
                        outStream.write(input);
                        outStream.flush();
                        outStream.close();
                        
                        for (BufferedFileListener listener : listeners) {
                            listener.addBufferedFile(bufferFile);
                        }
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 
     * @return is streaming
     */
    public boolean isStreaming() {
        return isStreaming;
    }
    /**
     * Starts streaming.
     */
    public void startStreaming() {
        isStreaming = true;
    }
    /**
     * Interrupts streaming.
     */
    public void stopStreaming() {
        isStreaming = false;
    }
    /**
     * Stops the thread.
     */
    public void stopThread() {
        isRunning = false;
    }
    /**
     * Adds a buffer files listener.
     * @param listener the listener to add
     */
    public void addBufferedFileListener(BufferedFileListener listener) {
        this.listeners.add(listener);
    }
}
