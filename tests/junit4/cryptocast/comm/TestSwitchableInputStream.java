package cryptocast.comm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.*;

import static org.junit.Assert.*;
import static cryptocast.util.ByteUtils.str2bytes;

public class TestSwitchableInputStream {
    @Test
    public void works() throws Exception {
        final InputStream[] streams = new InputStream[] {
            new ByteArrayInputStream(str2bytes("abc")),
            new ByteArrayInputStream(str2bytes("def")),
            new ByteArrayInputStream(str2bytes("ghi"))
        };
        
        final SwitchableInputStream switcher = new SwitchableInputStream();
        Thread t = new Thread(new Runnable() {
            @Override public void run() {
                for (InputStream in : streams) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) { throw new RuntimeException(); }
                    switcher.switchInput(in);
                }
            }
        });
        t.start();
        byte[] received = new byte[9];
        StreamUtils.readall(switcher, received, 0, 9);
        assertArrayEquals(str2bytes("abcdefghi"), received);
    }
}
