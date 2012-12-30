package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import org.junit.Test;

import cryptocast.comm.MessageBuffer;
import cryptocast.comm.StreamUtils;
import cryptocast.crypto.*;
import static cryptocast.util.ByteUtils.str2bytes;

public class TestBroadcastEncryptionCommunication extends WithNaorPinkasContext {
    private int t = 10;
    private NaorPinkasServer server = 
                NaorPinkasServer.generate(t, SchnorrGroup.getP1024Q160());
    private MessageBuffer fifo = new MessageBuffer();
    
    @Test
    public void serverCanSendToClient() throws Exception {
        BroadcastEncryptionServer<NaorPinkasIdentity> out = 
                BroadcastEncryptionServer.start(server, server, 256, fifo, 0, null);
        NaorPinkasPersonalKey key = server.getPersonalKey(server.getIdentity(0)).get();
        BroadcastEncryptionClient in =
                new BroadcastEncryptionClient(fifo, new NaorPinkasClient(key));
        byte[] payload = str2bytes("abcdefg");
        out.write(payload);
        out.close();
        byte[] read = new byte[payload.length + 10];
        assertEquals(payload.length, StreamUtils.readall(in, read, 0, read.length));
    }

    @Test
    public void serverBroadcastsKeyRegularly() throws Exception {
        BroadcastEncryptionServer<NaorPinkasIdentity> out = 
                BroadcastEncryptionServer.start(server, server, 256, fifo, 100, null);
        NaorPinkasPersonalKey key = server.getPersonalKey(server.getIdentity(0)).get();
        byte[] payload = str2bytes("abcdefg");
        out.write(payload);
        out.flush();
        fifo.setBlocking(false);
        while (fifo.recvMessage() != null) {}
        fifo.setBlocking(true);
        BroadcastEncryptionClient in =
                new BroadcastEncryptionClient(fifo, new NaorPinkasClient(key));
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
