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
import cryptocast.crypto.Protos.*;

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
        byte[] packedMsg = inner.recvMessage();
        if (packedMsg == null) {
            // stream should be terminated by a special EOF message
            throw new IOException("Unexpected EOF");
        }
        DynamicCipherMessage msg = DynamicCipherMessage.parseFrom(packedMsg);
        
        if (msg.hasPayloadMessage()) {
            if (cipher == null) {
                // ignore data before we got the key!
                return;
            }
            rest = cipher.update(msg.getPayloadMessage().getPayload().toByteArray());
        } else if (msg.hasKeyUpdateMessage()) {
            if (cipher != null) {
                finalizeCipher();
            }
            byte[] encryptedKey = msg.getKeyUpdateMessage().getEncryptedKey().toByteArray(),
                   iv = msg.getKeyUpdateMessage().getIv().toByteArray();
            if (!Arrays.equals(encryptedKey, lastEncryptedKey)) {
                try {
                    key = decodeKey(dec.decrypt(encryptedKey));
                } catch (DecryptionError e) {
                    throwWithCause(new IOException("Error while decrypting session key"), e);
                }
            }
            lastEncryptedKey = encryptedKey;
            try {
                cipher = createCipher(key, new IvParameterSpec(iv));
            } catch (InvalidKeyException e) {
                throwWithCause(new IOException("The other side sent an invalid session key"), e);
            } catch (InvalidAlgorithmParameterException e) {
                throwWithCause(new IOException("The other side sent an invalid IV"), e);
            }
            return;
        } else if (msg.hasEofMessage()) {
            finalizeCipher();
            eof = true;
        } else {
            log.warn("Empty message!");
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
