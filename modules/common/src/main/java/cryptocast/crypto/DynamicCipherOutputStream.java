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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cryptocast.comm.MessageOutChannel;

import static cryptocast.util.ErrorUtils.*;

/**
 * Represents a dynamic cipher output stream, which is an output stream that is decrypted upon writing.
 * This stream uses a message-based communication channel.
 */
public class DynamicCipherOutputStream extends OutputStream {
    private static final Logger log = LoggerFactory
            .getLogger(DynamicCipherOutputStream.class);
    
	/**
	 * Constant for cipher data.
	 */
    public static final byte CTRL_CIPHER_DATA = 0;
    /**
     * Constant for the update key.
     */
    public static final byte CTRL_UPDATE_KEY = 1;
    /**
     * Constant for the update key.
     */
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

    /**
     * Returns a new dynamic cipher output stream with the given values. 
     * 
     * @param inner The message-based communication channel.
     * @param keyBits key bits used to init a key generator for a certain size.
     * @param enc The encryption context.
     * @return a new dynamic cipher output stream.
     * @throws IOException
     */
    public static DynamicCipherOutputStream start(MessageOutChannel inner,
                                                  int keyBits,
                                                  Encryptor<byte[]> enc) 
                                      throws IOException {
        return new DynamicCipherOutputStream(inner, keyBits, enc);
    }

    /**
     * Updates the key.
     * @throws IOException
     */
    public void updateKey() throws IOException {
        log.trace("Updating key");
        key = keyGen.generateKey();
        encryptedKey = enc.encrypt(key.getEncoded());
        reinitializeCipher();
    }
    
    /**
     * Reinitializes the cipher.
     * @throws IOException
     */
    public void reinitializeCipher() throws IOException {
        log.trace("reinitializing cipher");
        if (cipher != null) {
            finalizeCipher();
        }
        try {
            cipher = createCipher(key);
        } catch (InvalidKeyException e) {
            cannotHappen(e); // because we generated the key by ourselves
        }
        DynamicCipherKeyUpdateMessage keyUpdate = 
                new DynamicCipherKeyUpdateMessage(encryptedKey, cipher.getIV());
        sendTypedMessage(CTRL_UPDATE_KEY, keyUpdate.pack());
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
        byte[] realMsg = new byte[msg.length + 1];
        realMsg[0] = type;
        System.arraycopy(msg, 0, realMsg, 1, msg.length);
        inner.sendMessage(realMsg);
    }
    
    private void sendTypedMessage(byte type) throws IOException {
        sendTypedMessage(type, new byte[0]);
    }

    private KeyGenerator createKeygen(int keyBits) {
        KeyGenerator gen = null;
        try {
            gen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            cannotHappen(e);
        }
        gen.init(keyBits);
        try {
            createCipher(gen.generateKey());
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(
                    "Creating a cipher failed, if you specified a key size over "
                  + "128 bits, please make sure that you have the Unlimited Strength "
                  + "Jurisdiction Policy Files installed", e);
        }
        return gen;
    }
    
    private Cipher createCipher(SecretKey key) throws InvalidKeyException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            cannotHappen(e);
        } catch (NoSuchPaddingException e) {
            cannotHappen(e);
        }
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher;
    }
}