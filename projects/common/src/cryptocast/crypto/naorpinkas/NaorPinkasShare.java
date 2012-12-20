package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import cryptocast.crypto.ModularExponentiationGroup;

/**
 * A share in the Naor-Pinkas broadcast encryption scheme. It consists of a tuple
 * $(r, I, g^{r P(I)})$. $t + 1$ distinct shares of this form are sufficient to restore the
 * value $g^{r P(0)}$, where $t$ is the degree of the polynomial $P$.
 */
public class NaorPinkasShare implements Comparable<NaorPinkasShare> {
    protected int t;
    protected BigInteger r, i, x;
    protected ModularExponentiationGroup group;

    protected NaorPinkasShare(int t, BigInteger r, BigInteger i, BigInteger x,
                              ModularExponentiationGroup group) {
        this.t = t;
        this.r = r;
        this.i = i;
        this.x = x;
        this.group = group;
    }
    
    @Override
    public int compareTo(NaorPinkasShare other) {
        return i.compareTo(other.i);
    }
}
