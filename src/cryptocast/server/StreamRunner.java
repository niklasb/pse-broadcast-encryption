package cryptocast.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamRunner implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(StreamRunner.class);
    private volatile boolean isRunning;
    private volatile boolean stopped;
    private InputStream in;
    private OutputStream out;
    private int bufsize;

    public StreamRunner(InputStream in, OutputStream out, int bufsize) {
        super();
        this.in = in;
        this.out = out;
        this.bufsize = bufsize;
        this.isRunning = false;
        this.stopped = true;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[bufsize];
        int received;
        stopped = false;
        isRunning = true;
        try {
            while ((received = in.read(buffer)) >= 0 && isRunning) {
                out.write(buffer, 0, received);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        stopped = true;
    }

    public void start() {
        new Thread(this).start();
        while(!isRunning) {
            //TODO wait until stream is finally started
        }
    }

    /**
     * Sets the flag to indicate that the thread running this runnable should stop working.
     */
    public void stop() {
        isRunning = false;
        while(!stopped) {
            //TODO wait until it really stops
        }
    }
    
    /**
     * @return weather this runner should run or not.
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * @return true if the thread running this runnable has stopped, else false
     */
    public boolean hasStopped() {
        return stopped;
    }
}
