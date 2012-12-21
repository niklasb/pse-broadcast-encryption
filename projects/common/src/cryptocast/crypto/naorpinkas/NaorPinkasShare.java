package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import cryptocast.crypto.ModularExponentiationGroup;
import static cryptocast.util.ByteUtils.*;

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
    
    public static NaorPinkasShare unpack(int t, 
                                        BigInteger r, 
                                        ModularExponentiationGroup group,
                                        ByteBuffer buf) {
        BigInteger i = getBigInt(buf),
                   x = getBigInt(buf);
        return new NaorPinkasShare(t, r, i, x, group);
    }

    public void pack(ByteBuffer buf) {
        putBigInt(buf, i);
        putBigInt(buf, x);
    }
}
