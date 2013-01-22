package cryptocast.crypto;

import java.io.IOException;
import java.io.InputStream;

import cryptocast.comm.MessageInChannel;

/**
 * The client side of a broadcast encryption scheme.
 */
public class BroadcastEncryptionClient extends InputStream {
    private DynamicCipherInputStream cipherStream;

    /**
     * Initializes a broadcast encryption client.
     * @param inner The message-based underlying communication channel.
     * @param dec The decryption context
     */
    public BroadcastEncryptionClient(MessageInChannel inner, Decryptor<byte[]> dec) 
                                           throws IOException {
        this.cipherStream = new DynamicCipherInputStream(inner, dec);
    }

    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException {
        return cipherStream.read(buffer, offset, len);
    }
    
    @Override
    public int read() throws IOException {
        return cipherStream.read();
    }
}