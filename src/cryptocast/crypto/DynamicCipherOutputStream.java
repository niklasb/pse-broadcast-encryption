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

import com.google.protobuf.ByteString;

import cryptocast.comm.MessageOutChannel;

import cryptocast.crypto.Protos.*;

import static cryptocast.util.ErrorUtils.*;

/**
 * Represents a dynamic cipher output stream, which is an output stream that is decrypted upon writing.
 * This stream uses a message-based communication channel.
 */
public class DynamicCipherOutputStream extends OutputStream {
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
        key = keyGen.generateKey();
        encryptedKey = enc.encrypt(key.getEncoded());
        reinitializeCipher();
    }
    
    /**
     * Reinitializes the cipher.
     * @throws IOException
     */
    public void reinitializeCipher() throws IOException {
        if (cipher != null) {
            finalizeCipher();
        }
        try {
            cipher = createCipher(key);
        } catch (InvalidKeyException e) {
            cannotHappen(e); // because we generated the key by ourselves
        }
        inner.sendMessage(
            DynamicCipherMessage.newBuilder().setKeyUpdateMessage(
                DynamicCipherKeyUpdateMessage.newBuilder()
                    .setEncryptedKey(ByteString.copyFrom(encryptedKey))
                    .setIv(ByteString.copyFrom(cipher.getIV()))
            ).build().toByteArray());
    }
    
    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        byte[] encData = cipher.update(data, offset, len);
        if (encData != null && encData.length > 0) {
            sendPayload(encData);
        }
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte)b });
    }

    @Override
    public void close() throws IOException {
        finalizeCipher();
        inner.sendMessage(
            DynamicCipherMessage.newBuilder().setEofMessage(
                DynamicCipherEofMessage.newBuilder()
            ).build().toByteArray());
    }

    private void finalizeCipher() throws IOException {
        try {
            sendPayload(cipher.doFinal());
        } catch (BadPaddingException e) {
            cannotHappen(e);
        } catch (IllegalBlockSizeException e) {
            cannotHappen(e);
        }
        flush();
    }

    private void sendPayload(byte[] payload) throws IOException {
        inner.sendMessage(
                DynamicCipherMessage.newBuilder().setPayloadMessage(
                    DynamicCipherPayloadMessage.newBuilder()
                        .setPayload(ByteString.copyFrom(payload))
                ).build().toByteArray());
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
            throwWithCause(new IllegalArgumentException(
                    "Creating a cipher failed, if you specified a key size over "
                  + "128 bits, please make sure that you have the Unlimited Strength "
                  + "Jurisdiction Policy Files installed"), e);
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