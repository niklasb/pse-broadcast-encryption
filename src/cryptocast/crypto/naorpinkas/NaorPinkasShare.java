package cryptocast.crypto.naorpinkas;

import cryptocast.crypto.*;
import java.math.BigInteger;

/**
 * A share in the Naor-Pinkas broadcast encryption scheme. It consists of a tuple
 * $(r, I, g^{r P(I)})$. $t + 1$ distinct shares of this form are sufficient to restore the
 * value $g^{r P(0)}$, where $t$ is the degree of the polynomial $P$.
 */
public class NaorPinkasShare implements Share<BigInteger> {
    /**
     * @return whether the share is complete and can be used to restore
     * the value.
     */
    public boolean isComplete() { return false; }
    /**
     * @return A binary representation of the share
     */
    public byte[] pack() { return null; }
    /**
     * Restore the value represented by this share.
     * @throws InsufficientInformationException if the share is incomplete
     * @return The restored value
     */
    public BigInteger restore() throws InsufficientInformationException { return null; }
}

