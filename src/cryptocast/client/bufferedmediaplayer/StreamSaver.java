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
    
    private static int FILE_SIZE = 100000;
    private static int INPUT_BUFFER_SIZE = FILE_SIZE / 100;
    private int bytesWritten = 0;
    
    private InputStream stream;
    private byte[] inputBuffer;

    private Queue<File> usableBufferFiles;
    private File currentBufferFile;
    
    private ArrayList<BufferedFileListener> listeners;

    private boolean isStreaming;
    private boolean isRunning;
    private int progress = 0;
    
    /**
     * Creates a StreamSaver which streams from the given stream and uses the given files as buffer.
     * @param stream the stream
     * @param usableBufferFiles the buffer files
     */
    public StreamSaver() {
        isRunning = true;
        inputBuffer = new byte[INPUT_BUFFER_SIZE];
        listeners = new ArrayList<BufferedFileListener>();
    }

    @Override
    public void run() {
        while(isRunning) {
            while (isStreaming) {
                if (!usableBufferFiles.isEmpty()) {
                    currentBufferFile = usableBufferFiles.peek();
                    bufferStreamInFile();
                    usableBufferFiles.remove(currentBufferFile);
                }
            }
        }
    }
    
    private void bufferStreamInFile() {
        try {
            FileOutputStream outStream = new FileOutputStream(currentBufferFile);
            bytesWritten = 0;

            while (bytesWritten < FILE_SIZE) {
                if (stream.available() >= INPUT_BUFFER_SIZE) {
                    stream.read(inputBuffer);
                    outStream.write(inputBuffer);
                    bytesWritten += inputBuffer.length;
                    progress = (int) ((float) bytesWritten * 100F / (float)FILE_SIZE);
                }
            }
            outStream.flush();
            outStream.close();
            
            for (BufferedFileListener listener : listeners) {
              listener.addBufferedFile(currentBufferFile);
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the buffer progress of the current buffering file in percent.
     * @return buffer progress
     */
    public int getBufferProgress() {
        return progress;
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
    /**
     * Prepares the streaming.
     * @param stream the stream
     * @param files the files to buffer the stream to
     */
    public void setStream(InputStream stream, Queue<File> files) {
        this.stream = stream;
        this.usableBufferFiles = files;
        
    }
}
