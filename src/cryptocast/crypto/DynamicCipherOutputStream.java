package cryptocast.crypto;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;

import cryptocast.comm.MessageOutChannel;
import cryptocast.util.ByteUtils;

import static cryptocast.util.ErrorUtils.cannotHappen;

public class DynamicCipherOutputStream extends OutputStream {
    public static final byte CTRL_CIPHER_DATA = 0;
    public static final byte CTRL_UPDATE_KEY = 1;
    public static final byte CTRL_EOF = 2;

    private MessageOutChannel inner;
    private SecretKey key;
    private KeyGenerator keyGen;
    private Cipher cipher;
    private Encryptor<byte[]> enc;
    byte[] encryptedKey;

    private DynamicCipherOutputStream(MessageOutChannel inner,
                                      int keyBits,
                                      Encryptor<byte[]> enc) throws IOException {
        this.inner = inner;
        this.enc = enc;
        keyGen = createKeygen(keyBits);
        updateKey();
    }

    public static DynamicCipherOutputStream start(MessageOutChannel inner,
                                                  int keyBits,
                                                  Encryptor<byte[]> enc) 
                                      throws IOException {
        return new DynamicCipherOutputStream(inner, keyBits, enc);
    }

    public void updateKey() throws IOException {
        key = keyGen.generateKey();
        encryptedKey = enc.encrypt(key.getEncoded());
        reinitializeCipher();
    }
    
    public void reinitializeCipher() throws IOException {
        if (cipher != null) {
            finalizeCipher();
        }
        try {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e) {
            cannotHappen(e);
        } catch (NoSuchPaddingException e) {
            cannotHappen(e);
        } catch (InvalidKeyException e) {
            cannotHappen(e); // because we generated the key by ourselves!
        }
        DynamicCipherKeyUpdateMessage keyUpdate = 
                new DynamicCipherKeyUpdateMessage(encryptedKey, cipher.getIV());
        sendTypedMessage(CTRL_UPDATE_KEY, ByteUtils.pack(keyUpdate));
    }
    
    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        byte[] encData = cipher.update(data, offset, len);
        if (encData != null && encData.length > 0) {
            sendTypedMessage(CTRL_CIPHER_DATA, encData);
        }
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte)b });
    }

    @Override
    public void close() throws IOException {
        finalizeCipher();
        sendTypedMessage(CTRL_EOF);
    }

    private void finalizeCipher() throws IOException {
        try {
            sendTypedMessage(CTRL_CIPHER_DATA, cipher.doFinal());
        } catch (BadPaddingException e) {
            cannotHappen(e);
        } catch (IllegalBlockSizeException e) {
            cannotHappen(e);
        }
        flush();
    }

    private void sendTypedMessage(byte type, byte[] msg) throws IOException {
        //System.out.println("[server] sending message type=" + type + " length=" + msg.length);
        byte[] realMsg = new byte[msg.length + 1];
        realMsg[0] = type;
        System.arraycopy(msg, 0, realMsg, 1, msg.length);
        inner.sendMessage(realMsg);
    }
    
    private void sendTypedMessage(byte type) throws IOException {
        sendTypedMessage(type, new byte[0]);
    }

    private KeyGenerator createKeygen(int keyBits) {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(keyBits);
            return gen;
        } catch (NoSuchAlgorithmException e) {
            cannotHappen(e);
        }
        return null; // just to make the compiler happy
    }
}