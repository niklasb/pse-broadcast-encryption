package cryptocast.crypto.naorpinkas;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * An identity in the Naor-Pinkas broadcast encryption scheme
 */
public class NPIdentity implements Serializable {
    private static final long serialVersionUID = -4137161426277758109L;

    BigInteger i;

    /**
     * initializes an instance.
     * @param i The identity code.
     */
    protected NPIdentity(BigInteger i) {
        this.i = i;
    }

    /**
     * Returns the identity code.
     *
     * @return identity code.
     */
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
        return ((NPIdentity)other).i.equals(i);
    }
}
