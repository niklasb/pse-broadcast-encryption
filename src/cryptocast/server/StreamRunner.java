package cryptocast.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.comm.StreamUtils;

public class StreamRunner implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(StreamRunner.class);
    private volatile boolean isRunning;
    private volatile boolean stopped;
    private InputStream in;
    private OutputStream out;
    private int bufsize;
    
    @Override
    public void run() {
        isRunning = true;
        stopped = false;
        try {
            StreamUtils.copyInterruptable(in, out, bufsize, this);
        } catch (IOException e) {
            log.error("Stream crashed", e);
        }
        stopped = true;
    }
    
    public StreamRunner() {
        this.isRunning = false;
        this.stopped = true;
    }
    
    public StreamRunner(InputStream in, OutputStream out, int bufsize) {
        super();
        this.in = in;
        this.out = out;
        this.bufsize = bufsize;
        this.isRunning = false;
        this.stopped = true;
    }

    /**
     * Sets the flag to indicate that the thread running this runnable should stop working.
     */
    public void stop() {
        isRunning = false;
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
