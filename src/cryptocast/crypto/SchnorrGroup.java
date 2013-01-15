package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

import static cryptocast.util.ByteUtils.*;

import static com.google.common.base.Preconditions.*;

/**
 * Represents a schnorr group.
 */
public class SchnorrGroup extends CyclicGroupOfPrimeOrder<BigInteger>
                          implements Serializable {
    private static final long serialVersionUID = -2980881642885431015L;

    private BigInteger p;
    private IntegersModuloPrime modP;

    /**
     * Creates a schnorr group with the given values:
     * $q$ and $p$ prime, $p = qr + 1$ and $g$ generates
     * a subgroup of $\mathbb{Z}_p^\times$ with order $q$.
     * @param p $p$, a large prime
     * @param q $q$, a prime factor of $p - 1$
     * @param g $g$, the generator of a subgroup of $\mathbb{Z}_p^\times$ 
     *          with order $q$
     */
    public SchnorrGroup(BigInteger p, BigInteger q, BigInteger g) {
        super(g, q);
        checkArgument(p.subtract(BigInteger.ONE).mod(q).equals(BigInteger.ZERO),
                          "q must divide (p - 1) without remainder");
        this.p = p;
        modP = new IntegersModuloPrime(p);
    }

    @Override
    public BigInteger combine(BigInteger a, BigInteger b) {
        return modP.multiply(a, b);
    }

    @Override
    public BigInteger pow(BigInteger a, BigInteger k) {
        return modP.pow(a, k);
    }

    @Override
    public BigInteger invert(BigInteger a) {
        return modP.invert(a);
    }

    @Override
    public BigInteger identity() {
        return modP.one();
    }

    /** @return $p$ */
    public BigInteger getP() {
        return p;
    }

    /** @return the field of integers modulo $p$ */
    public IntegersModuloPrime getFieldModP() {
        return modP;
    }
    
    /**
     * Initializes a 1024-bit Diffie-Hellman MODP group
     * with a 160-bit prime order subgroup.
     * It is taken from http://tools.ietf.org/html/rfc5114#section-2.1
     * @return The group
     */
    public static SchnorrGroup getP1024Q160() {
        BigInteger p = hexStringToBigInt(
            "B10B8F96 A080E01D DE92DE5E AE5D54EC 52C99FBC FB06A3C6" +
            "9A6A9DCA 52D23B61 6073E286 75A23D18 9838EF1E 2EE652C0" +
            "13ECB4AE A9061123 24975C3C D49B83BF ACCBDD7D 90C4BD70" +
            "98488E9C 219A7372 4EFFD6FA E5644738 FAA31A4F F55BCCC0" +
            "A151AF5F 0DC8B4BD 45BF37DF 365C1A65 E68CFDA7 6D4DA708" +
            "DF1FB2BC 2E4A4371"
            );
        BigInteger q = hexStringToBigInt(
            "F518AA87 81A8DF27 8ABA4E7D 64B7CB9D 49462353"
            );
        BigInteger g = hexStringToBigInt(
            "A4D1CBD5 C3FD3412 6765A442 EFB99905 F8104DD2 58AC507F" +
            "D6406CFF 14266D31 266FEA1E 5C41564B 777E690F 5504F213" +
            "160217B4 B01B886A 5E91547F 9E2749F4 D7FBD7D3 B9A92EE1" +
            "909D0D22 63F80A76 A6A24C08 7A091F53 1DBF0A01 69B6A28A" +
            "D662A4D1 8E73AFA3 2D779D59 18D08BC8 858F4DCE F97C2A24" +
            "855E6EEB 22B3B2E5"
            );
        return new SchnorrGroup(p, q, g);
    }
    
    private static BigInteger hexStringToBigInt(String hex) {
        return new BigInteger(hex.replaceAll("\\s+", ""), 16);
    }
    
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        SchnorrGroup other = (SchnorrGroup)other_;
        return p.equals(other.p)
            && getGenerator().equals(other.getGenerator())
            && getOrder().equals(other.getOrder());
    }
}