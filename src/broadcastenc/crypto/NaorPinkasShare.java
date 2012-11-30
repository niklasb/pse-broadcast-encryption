package broadcastenc.crypto;

import java.math.BigInteger;

public class NaorPinkasShare implements Share<BigInteger> {
    public NaorPinkasShare() {}
    public boolean isComplete() { return false; }
    public byte[] pack() { return null; }
    public BigInteger restore() throws InsufficientInformationException {
        return null;
    }
}

