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
    private StreamRunner sut;
    private final int BUFSIZE = 1024;
    @Mock private OutputStream out;
    @Mock private InputStream in;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        sut = new StreamRunner();
        assertTrue(sut.hasStopped());
        assertFalse(sut.isRunning());
        sut.prepare(in, out, BUFSIZE);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void startAndStop() throws InterruptedException {
        sut.start();
        assertTrue(sut.isRunning());
        assertFalse(sut.hasStopped());
        sut.stop();
        assertTrue(sut.hasStopped());
        assertFalse(sut.isRunning());
    }

}
