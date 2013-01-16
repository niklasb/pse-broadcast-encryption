package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import cryptocast.comm.MessageBuffer;
import cryptocast.comm.StreamUtils;
import cryptocast.crypto.*;
import static cryptocast.util.ByteUtils.str2bytes;

public abstract class TestNPBroadcastEnc
           <T, G extends CyclicGroupOfPrimeOrder<T>> {
    protected abstract NPServer<T, G> makeServer(int t);
    protected abstract NPClient<T, G> makeClient(NPKey<T, G> key);
    
    private int t = 10;
    protected NPServer<T, G> server = makeServer(t);
    
    private MessageBuffer fifo = new MessageBuffer();
    
    @Test
    public void serverCanSendToClient() throws Exception {
        BroadcastEncryptionServer<NPIdentity> out = 
                BroadcastEncryptionServer.start(server, server, 128, fifo, 0, null);
        NPKey<T, G> key = 
                server.getPersonalKey(server.getIdentity(0)).get();
        BroadcastEncryptionClient in =
                new BroadcastEncryptionClient(fifo, makeClient(key));
        byte[] payload = str2bytes("abcdefg");
        out.write(payload);
        out.close();
        byte[] read = new byte[payload.length + 10];
        assertEquals(payload.length, StreamUtils.readall(in, read, 0, read.length));
    }

    @Test
    public void serverBroadcastsKeyRegularly() throws Exception {
        BroadcastEncryptionServer<NPIdentity> out = 
                BroadcastEncryptionServer.start(server, server, 128, fifo, 100, null);
        NPKey<T, G> key = 
                server.getPersonalKey(server.getIdentity(0)).get();
        byte[] payload = str2bytes("abcdefg");
        out.write(payload);
        out.flush();
        fifo.setBlocking(false);
        while (fifo.recvMessage() != null) {}
        fifo.setBlocking(true);
        BroadcastEncryptionClient in =
                new BroadcastEncryptionClient(fifo, makeClient(key));
        Thread worker = new Thread(out);
        worker.start();
        Thread.sleep(500);
        out.write(payload);
        out.close();
        byte[] read = new byte[payload.length + 10];
        assertEquals(payload.length, StreamUtils.readall(in, read, 0, read.length));
        worker.interrupt();
        worker.join();
    }
}
