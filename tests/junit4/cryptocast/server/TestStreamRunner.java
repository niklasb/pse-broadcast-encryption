package cryptocast.server;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestStreamRunner {
    private Thread thread;
    private StreamRunner sut;
    @Mock private OutputStream out;
    @Mock private InputStream in;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        sut = new StreamRunner(in, out, 1024);
        thread = new Thread(sut);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void startAndStop() {
        thread.start();
        assertTrue(sut.isRunning());
        assertFalse(sut.hasStopped());
        sut.stop();
        assertTrue(sut.hasStopped());
        assertFalse(sut.isRunning());
    }

}
