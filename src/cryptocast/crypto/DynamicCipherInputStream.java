package cryptocast.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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

import cryptocast.comm.MessageInChannel;
import cryptocast.util.ByteUtils;

import static cryptocast.util.ErrorUtils.*;

public class DynamicCipherInputStream extends InputStream {
    MessageInChannel inner;
    SecretKey key;
    byte[] lastEncryptedKey;
    Cipher cipher;
    PipedInputStream pipeIn;
    PipedOutputStream pipeOut;
    private Decryptor<byte[]> dec;
    boolean eof = false;
    
    public DynamicCipherInputStream(MessageInChannel inner,
                                    Decryptor<byte[]> dec) throws IOException {
        this.inner = inner;
        this.dec = dec;
        this.pipeIn = new PipedInputStream();
        this.pipeOut = new PipedOutputStream(pipeIn);
    }

    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException {
        while (!eof && pipeIn.available() == 0) {
            processMessage();
        }
        return pipeIn.read(buffer, offset, len);
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
        //System.out.println("[client] processMessage");
        byte[] msg = inner.recvMessage();
        assert msg != null;
        //System.out.println("[client] received message type=" + msg[0] + " length=" + (msg.length - 1));
        
        switch (msg[0]) { // switch on the message type
        case DynamicCipherOutputStream.CTRL_CIPHER_DATA:
            if (cipher == null) {
                // ignore data before we got the key!
                return;
                //throw new IllegalStateException("Cannot yet decrypt data");
            }
            byte[] plainData = cipher.update(msg, 1, msg.length - 1);
            if (plainData != null) {
                pipeOut.write(plainData);
            }
            return;
        case DynamicCipherOutputStream.CTRL_UPDATE_KEY:
            if (cipher != null) {
                finalizeCipher();
            }
            DynamicCipherKeyUpdateMessage keyUpdate = 
                    DynamicCipherKeyUpdateMessage.unpack(
                            ByteUtils.startUnpack(msg, 1, msg.length - 1));
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
            pipeOut.close();
            eof = true;
            return;
        default:
            throw new IOException("Invalid message type: " + msg[0]);
        }
    }

    private void finalizeCipher() throws IOException {
        try {
            pipeOut.write(cipher.doFinal());
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
