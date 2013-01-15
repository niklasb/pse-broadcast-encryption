package cryptocast.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.comm.MessageInChannel;
import cryptocast.util.ArrayUtils;

import static cryptocast.util.ErrorUtils.*;

/**
 * Represents a dynamic cipher input stream, which is an input stream that needs to be decrypted.
 * This stream uses a message-based communication channel.
 */
public class DynamicCipherInputStream extends InputStream {
    private static final Logger log = LoggerFactory
            .getLogger(DynamicCipherInputStream.class);
    
    MessageInChannel inner;
    SecretKey key;
    byte[] lastEncryptedKey;
    Cipher cipher;
    private Decryptor<byte[]> dec;
    byte[] rest;
    boolean eof = false;
    
    /**
     * Initializes a new instance of DynamicCipherInputStream.
     * 
     * @param inner The message-based underlying communication channel.
     * @param dec The decryption context.
     * @throws IOException
     */
    public DynamicCipherInputStream(MessageInChannel inner,
                                    Decryptor<byte[]> dec) throws IOException {
        this.inner = inner;
        this.dec = dec;
    }

    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException {
        while (!eof && (rest == null || rest.length == 0)) {
            processMessage();
        }
        if (eof && (rest == null || rest.length == 0)) {
            return -1;
        }
        int size = Math.min(len, rest.length);
        assert size > 0;
        System.arraycopy(rest, 0, buffer, offset, size);
        if (size < rest.length) {
            rest = ArrayUtils.copyOfRange(rest, size, rest.length);
        } else {
            rest = null;
        }
        return size;
    }
    
    @Override
    public int read() throws IOException {
        byte[] buf = new byte[1];
        if (read(buf, 0, 1) < 0) { 
            return -1; 
        }
        return buf[0];
    }

    private void processMessage() throws IOException {
        byte[] msg = inner.recvMessage();
        if (msg == null) {
            // stream should be terminated by a special EOF message
            throw new IOException("Unexpected EOF");
        }
        
        switch (msg[0]) { // switch on the message type
        case DynamicCipherOutputStream.CTRL_CIPHER_DATA:
            if (cipher == null) {
                // ignore data before we got the key!
                return;
                //throw new IllegalStateException("Cannot yet decrypt data");
            }
            rest = cipher.update(msg, 1, msg.length - 1);
            return;
        case DynamicCipherOutputStream.CTRL_UPDATE_KEY:
            if (cipher != null) {
                finalizeCipher();
            }
            DynamicCipherKeyUpdateMessage keyUpdate = 
                    DynamicCipherKeyUpdateMessage.unpack(
                            ArrayUtils.copyOfRange(msg, 1, msg.length));
            if (!Arrays.equals(keyUpdate.getEncryptedKey(), lastEncryptedKey)) {
                try {
                    key = decodeKey(dec.decrypt(keyUpdate.getEncryptedKey()));
                } catch (DecryptionError e) {
                    throwWithCause(new IOException("Error while decrypting session key"), e);
                }
            }
            lastEncryptedKey = keyUpdate.getEncryptedKey();
            try {
                cipher = createCipher(key, new IvParameterSpec(keyUpdate.getIv()));
            } catch (InvalidKeyException e) {
                throwWithCause(new IOException("The other side sent an invalid session key"), e);
            } catch (InvalidAlgorithmParameterException e) {
                throwWithCause(new IOException("The other side sent an invalid IV"), e);
            }
            return;
        case DynamicCipherOutputStream.CTRL_EOF:
            finalizeCipher();
            eof = true;
            return;
        default:
            throw new IOException("Invalid message type: " + msg[0]);
        }
    }

    private void finalizeCipher() throws IOException {
        try {
            rest = cipher.doFinal();
        } catch (BadPaddingException e) {
            cannotHappen(e);
        } catch (IllegalBlockSizeException e) {
            cannotHappen(e);
        }
    }

    private SecretKey decodeKey(byte[] enc) {
        return new SecretKeySpec(enc, "AES");
    }

    private Cipher createCipher(SecretKey key, IvParameterSpec iv) 
                throws InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            cannotHappen(e);
        } catch (NoSuchPaddingException e) {
            cannotHappen(e);
        }
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher;
    }
}
