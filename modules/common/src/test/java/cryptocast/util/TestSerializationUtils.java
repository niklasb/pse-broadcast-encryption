package cryptocast.util;

import static org.junit.Assert.*;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

public class TestSerializationUtils {
    @Test
    public void writeAndReadWorks() throws Exception {
        String obj = "foobar";
        PipedOutputStream pipeOut = new PipedOutputStream();
        PipedInputStream pipeIn = new PipedInputStream(pipeOut);
        SerializationUtils.writeToStream(pipeOut, obj);
        assertEquals(obj, SerializationUtils.readFromStream(pipeIn));
    }
}
