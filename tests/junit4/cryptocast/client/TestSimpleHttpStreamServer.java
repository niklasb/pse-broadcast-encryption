package cryptocast.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;

import org.junit.*;
import static org.junit.Assert.*;

import static cryptocast.util.ByteUtils.str2bytes;
import cryptocast.comm.StreamUtils;

public class TestSimpleHttpStreamServer {
    @Test
    public void streamsCorrectly() throws Exception {
        byte[] payload = str2bytes("asdqow3u23yqwkleh12iu31ejhasdh123");
        InputStream in = new ByteArrayInputStream(payload);
        SimpleHttpStreamServer sut = new SimpleHttpStreamServer(
                in, new InetSocketAddress("127.0.0.1", 21312), 
                "application/octet-stream", 
                5);
        Thread t = new Thread(sut);
        t.start();
        InputStream raw = new URL("http://localhost:21312/")
                             .openConnection()
                             .getInputStream();
        byte[] actual = new byte[payload.length + 10];
        assertEquals(payload.length, StreamUtils.readall(raw, actual, 0, actual.length));
        t.interrupt();
        assertFalse(t.isAlive());
    }
}
