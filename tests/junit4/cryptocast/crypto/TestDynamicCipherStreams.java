package cryptocast.crypto;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import cryptocast.comm.*;
import static cryptocast.util.ByteUtils.str2bytes;
import cryptocast.util.ArrayUtils;
import cryptocast.util.TestUtils;

public class TestDynamicCipherStreams {
    MessageBuffer fifo = new MessageBuffer();
    DynamicCipherOutputStream out;
    DynamicCipherInputStream in;
    Encryptor<byte[]> enc;
    Decryptor<byte[]> dec;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        enc = mock(Encryptor.class, TestUtils.RETURNS_FIRST_ARGUMENT());
        dec = mock(Decryptor.class, TestUtils.RETURNS_FIRST_ARGUMENT());
        out = DynamicCipherOutputStream.start(fifo, 256, enc);
        in = new DynamicCipherInputStream(fifo, dec);
    }
    
    @Test
    public void basicEncryptDecryptWorks() throws Exception {
        byte[] payload = str2bytes("abcdefg");
        out.write(payload);
        out.close();
        byte[] read = new byte[payload.length + 10];
        assertEquals(payload.length, StreamUtils.readall(in, read, 0, read.length));
        assertArrayEquals(payload, ArrayUtils.copyOfRange(read, 0, payload.length));
    }

    @Test
    public void clientCanReadPartialData() throws Exception {
        byte[] payload = str2bytes("abcdefg");
        out.write(payload);
        out.flush();
        assertEquals((byte)'a', in.read());
    }
    
    @Test
    public void clientIgnoresDataBeforeKey() throws Exception {
        out.write(str2bytes("foo"));
        out.write(str2bytes("bar"));
        out.write(str2bytes("baz"));
        fifo.setBlocking(false);
        while (fifo.recvMessage() != null) { }
        fifo.setBlocking(true);

        byte[] payload = str2bytes("abcdefg");
        out.reinitializeCipher();
        out.write(payload);
        out.close();
        
        byte[] read = new byte[payload.length + 10];
        assertEquals(payload.length, StreamUtils.readall(in, read, 0, read.length));
        assertArrayEquals(payload, ArrayUtils.copyOfRange(read, 0, payload.length));
    }
    
    @Test
    public void keyUpdateWorks() throws Exception {
        String payload = "abcdefg";
        out.write(str2bytes(payload));
        out.updateKey();
        out.write(str2bytes(payload));
        out.updateKey();
        out.write(str2bytes(payload));
        out.close();
        byte[] expected = str2bytes(payload + payload + payload);
        byte[] read = new byte[expected.length + 10];
        assertEquals(expected.length, StreamUtils.readall(in, read, 0, read.length));
        assertArrayEquals(expected, ArrayUtils.copyOfRange(read, 0, expected.length));
    }
    
    @Test
    public void keyBroadcastWorks() throws Exception {
        String payload = "abcdefg";
        out.write(str2bytes(payload));
        out.reinitializeCipher();
        out.write(str2bytes(payload));
        out.reinitializeCipher();
        out.write(str2bytes(payload));
        out.close();
        byte[] expected = str2bytes(payload + payload + payload);
        byte[] read = new byte[expected.length + 10];
        assertEquals(expected.length, StreamUtils.readall(in, read, 0, read.length));
        assertArrayEquals(expected, ArrayUtils.copyOfRange(read, 0, expected.length));
        verify(enc, times(1)).encrypt(any(byte[].class));
        verify(dec, times(1)).decrypt(any(byte[].class));
    }
}