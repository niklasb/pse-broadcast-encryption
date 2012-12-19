package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

/**
 * An identity in the Naor-Pinkas broadcast encryption scheme
 */
public class NaorPinkasIdentity {
    BigInteger id;

    private NaorPinkasIdentity(BigInteger id) {
        this.id = id;
    }

    private BigInteger getId() { return id; }
}
