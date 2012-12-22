package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

/**
 * An identity in the Naor-Pinkas broadcast encryption scheme
 */
public class NaorPinkasIdentity {
    BigInteger i;

    protected NaorPinkasIdentity(BigInteger i) {
        this.i = i;
    }

    protected BigInteger getI() { return i; }
    
    @Override
    public int hashCode() {
        return i.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) { return false; }
        return ((NaorPinkasIdentity)other).i.equals(i);
    }
}
