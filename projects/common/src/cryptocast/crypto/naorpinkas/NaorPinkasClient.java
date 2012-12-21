package cryptocast.crypto.naorpinkas;

import static cryptocast.util.ByteUtils.*;
import cryptocast.crypto.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InsufficientResourcesException;

import com.google.common.base.Optional;

/**
 * A client in the Naor-Pinkas broadcast encryption scheme.
 */
public class NaorPinkasClient implements Decryptor<byte[]> {
    class Message {
        BigInteger xor;
        BigInteger r;
        List<NaorPinkasShare> shares;
        
        Message(BigInteger r, BigInteger xor, List<NaorPinkasShare> shares) {
            this.r = r;
            this.xor = xor;
            this.shares = shares;
            
        }
    }

    private NaorPinkasPersonalKey key;
    private NaorPinkasShareCombinator combinator =
                 new NaorPinkasShareCombinator();
    
    /**
     * Initializes a Naor-Pinkas broadcast client.
     * @param key The personal key used to reconstruct a secret from the stream.
     */
    public NaorPinkasClient(NaorPinkasPersonalKey key) {
        this.key = key;
    }

    /**
      * Decrypts a secret.
      * @param cipher The encrypted secret
      * @return The decrypted secret
      */
    public byte[] decrypt(byte[] cipher) throws InsufficientInformationError {
        Message msg = parseMessage(cipher);
        List<NaorPinkasShare> shares = msg.shares;
        shares.add(key.getShare(msg.r));
        Optional<BigInteger> mInterpol = combinator.restore(shares);
        if (!mInterpol.isPresent()) {
            throw new InsufficientInformationError(
                    "Cannot restore secret: Redundant or missing information");
        }
        return mInterpol.get().xor(msg.xor).toByteArray();
    }

    private Message parseMessage(byte[] msg) {
        ByteBuffer buf = ByteBuffer.wrap(msg);
        buf.order(ByteOrder.BIG_ENDIAN);
        int t = buf.getInt();
        BigInteger r = getBigInt(buf),
                   xor = getBigInt(buf),
                   p = getBigInt(buf),
                   g = getBigInt(buf);
        ModularExponentiationGroup group = new ModularExponentiationGroup(g, p);
        List<NaorPinkasShare> shares = new ArrayList<NaorPinkasShare>(t + 1);
        for (int i = 0; i < t; ++i) {
            shares.add(NaorPinkasShare.unpack(t, r, group, buf));
        }
        return new Message(r, xor, shares);
    }
}
