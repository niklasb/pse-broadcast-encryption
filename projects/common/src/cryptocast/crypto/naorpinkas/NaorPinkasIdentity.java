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
}
