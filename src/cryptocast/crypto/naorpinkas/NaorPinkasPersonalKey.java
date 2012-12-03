package cryptocast.crypto.naorpinkas;

import java.security.PrivateKey;

public class NaorPinkasPersonalKey implements PrivateKey {
    public String getAlgorithm() { return "Naor-Pinkas"; }
    public byte[] getEncoded() { return null; }
    public String getFormat() { return null; }
}
