package cryptocast.crypto.naorpinkas;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * An identity in the Naor-Pinkas broadcast encryption scheme
 */
public class NaorPinkasIdentity implements Serializable {
    private static final long serialVersionUID = -4137161426277758109L;
    
    BigInteger i;

    protected NaorPinkasIdentity(BigInteger i) {
        this.i = i;
    }

    protected BigInteger getI() { 
        return i; 
    }
    
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
