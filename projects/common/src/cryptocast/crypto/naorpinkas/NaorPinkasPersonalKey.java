package cryptocast.crypto.naorpinkas;

import java.security.PrivateKey;

/**
 * A user's personal key in the Naor-Pinkas broadcast encryption scheme.
 */
public class NaorPinkasPersonalKey implements PrivateKey {
    public String getAlgorithm() { return "Naor-Pinkas"; }
    public byte[] getEncoded() { return null; }
    public String getFormat() { return null; }
}
