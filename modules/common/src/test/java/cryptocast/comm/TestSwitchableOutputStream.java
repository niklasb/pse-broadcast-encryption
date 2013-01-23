package cryptocast.comm;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import static cryptocast.util.ByteUtils.*;

public class TestSwitchableOutputStream {
    @Test
    public void works() throws Exception {
        ByteArrayOutputStream[] streams = {
            new ByteArrayOutputStream(),
            new ByteArrayOutputStream(),
            new ByteArrayOutputStream(),
        };
        SwitchableOutputStream sut = new SwitchableOutputStream();
        byte[] payload = str2bytes("abc");
        sut.write(payload);
        for (int i = 0; i < streams.length; ++i) {
            sut.switchOutput(streams[i]);
            sut.write(payload);
        }
        for (int i = 0; i < streams.length; ++i) {
            assertArrayEquals(payload, streams[i].toByteArray());
        }
    }
}
