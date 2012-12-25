package cryptocast.crypto.naorpinkas.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cryptocast.comm.MessageBuffer;
import cryptocast.comm.StreamUtils;
import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
import static cryptocast.util.ByteUtils.str2bytes;

public class TestBroadcastEncryptionCommunication extends WithNaorPinkasContext {
    private int t = 10;
    private NaorPinkasServer server = 
                NaorPinkasServer.generate(t, SchnorrGroup.getP1024Q160());
    private MessageBuffer fifo = new MessageBuffer();
    
    @Test
    public void serverCanSendToClient() throws Exception {
        BroadcastEncryptionServer<NaorPinkasIdentity> out = 
                BroadcastEncryptionServer.start(server, server, 256, fifo);
        NaorPinkasPersonalKey key = server.getPersonalKey(server.getIdentity(0)).get();
        BroadcastEncryptionClient in =
                new BroadcastEncryptionClient(fifo, new NaorPinkasClient(key));
        byte[] payload = str2bytes("abcdefg");
        out.write(payload);
        out.close();
        byte[] read = new byte[payload.length + 10];
        assertEquals(payload.length, StreamUtils.readall(in, read, 0, read.length));
    }
}
