package cryptocast.crypto;

import cryptocast.comm.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * The client side of a broadcast encryption scheme.
 */
public class BroadcastEncryptionClient extends InputStream {
    private static int BUFSIZE = 4096 * 1024;
    byte[] buffer = new byte[BUFSIZE];
    
    /**
     * Initializes a broadcast encryption client.
     * @param inner The message-based underlying communication channel.
     * @param dec The decryption context
     */
    public BroadcastEncryptionClient(StreamMessageInChannel inner,
                                     Decryptor<BigInteger> dec) { }

    @Override
    public int read() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }
}
