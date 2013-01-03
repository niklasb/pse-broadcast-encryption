package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import static cryptocast.util.ByteUtils.*;
import cryptocast.util.Packable;

import static com.google.common.base.Preconditions.*;

public class SchnorrGroup implements Packable, Serializable {
    private static final long serialVersionUID = -2980881642885431015L;
    
    private BigInteger p, q, r, g;
    private IntegersModuloPrime modP, modQ;

    public SchnorrGroup(BigInteger p, BigInteger q, BigInteger g) {
        checkArgument(p.subtract(BigInteger.ONE).mod(q).equals(BigInteger.ZERO),
                          "q must divide (p - 1) without remainder");
        this.p = p;
        this.q = q;
        this.r = p.subtract(BigInteger.ONE).divide(q);
        this.g = g;
        modP = new IntegersModuloPrime(p);
        modQ = new IntegersModuloPrime(q);
    }
    
    public IntegersModuloPrime getFieldModP() {
        return modP;
    }
    
    public IntegersModuloPrime getFieldModQ() {
        return modQ;
    }

    /** @return $p$ */
    public BigInteger getP() {
        return p;
    }
    
    /** @return $g$ */
    public BigInteger getG() {
        return g;
    }
   
    /** @return $q$ */
    public BigInteger getQ() {
        return q;
    }
    
    /** @return $r$ */
    public BigInteger getR() {
        return r;
    }
   
    /** @return $g^k$ */
    public BigInteger getPowerOfG(BigInteger k) {
        return modP.pow(g, k);
    }

    @Override
    public int getMaxSpace() {
        return 3 * modP.getMaxNumberSpace();
    }
    
    @Override
    public void pack(ByteBuffer buf) {
        putBigInt(buf, p);
        putBigInt(buf, q);
        putBigInt(buf, g);
    }

    public static SchnorrGroup unpack(ByteBuffer buf) {
        BigInteger p = getBigInt(buf),
                   q = getBigInt(buf),
                   g = getBigInt(buf);
        return new SchnorrGroup(p, q, g);
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
            && g.equals(other.g)
            && q.equals(other.q);
    }
}