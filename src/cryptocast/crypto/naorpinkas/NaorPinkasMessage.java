package cryptocast.crypto.naorpinkas;

import static cryptocast.util.ByteUtils.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;
import cryptocast.util.MapUtils;
import cryptocast.util.Packable;
/**
 * This class represents a naor-pinkas message.
 */
public class NaorPinkasMessage implements Packable {
    private BigInteger xor;
    private BigInteger r;
    private SchnorrGroup schnorr;
    int t;
    private LagrangeInterpolation<BigInteger> lagrange;
    private ImmutableList<NaorPinkasShare> shares;

    /**
     * Creates a new instance of NaorPinkasMessage with the given parameters.
     * 
     * @param t The degree of the polynomial.
     * @param r The $r$ value.
     * @param xor The $xor$ value.
     * @param schnorr The schnorr group.
     * @param lagrange The corresponding lagrange interpolation.
     * @param shares The shares.
     */
    public NaorPinkasMessage(int t, BigInteger r, BigInteger xor, 
                             SchnorrGroup schnorr,
                             LagrangeInterpolation<BigInteger> lagrange,
                             ImmutableList<NaorPinkasShare> shares) {
        assert schnorr.getFieldModQ().equals(lagrange.getField());
        assert shares.size() == t;
        this.t = t;
        this.r = r;
        this.xor = xor;
        this.schnorr = schnorr;
        this.lagrange = lagrange;
        this.shares = shares;
    }

    /**
     * Returns the $xor$ value.
     * 
     * @return The $xor$ value.
     */
    public BigInteger getXor() { return xor; }
    /**
     * Returns the $r$ value.
     * 
     * @return The $r$ value.
     */
    public BigInteger getR() { return r; }
    /**
     * Returns the shares for this message.
     * 
     * @return The shares.
     */
    public ImmutableList<NaorPinkasShare> getShares() { return shares; }
    /**
     * Returns the lagrange interpolation for this message.
     * 
     * @return The lagrange interpolation.
     */
    public LagrangeInterpolation<BigInteger> getLagrange() { return lagrange; }

    /**
     * Returns the maximum space.
     */
    public int getMaxSpace() {
        int size = 
                 // t
                   4 
                 // r, xor
                 + 2 * schnorr.getFieldModP().getMaxNumberSpace()
                 // packing of the schnorr group
                 + schnorr.getMaxSpace()
                 // lagrange coefficients
                 + shares.size() * schnorr.getFieldModQ().getMaxNumberSpace();
        if (shares.size() > 0) {
            size += shares.size() * shares.get(0).getMaxSpace();
        }
        return size;
    }

   /**
    * Packs the message into the given byte buffer.
    * 
    * @param buf A byte buffer.
    */
    public void pack(ByteBuffer buf) {
        buf.putInt(t);
        putBigInt(buf, r);
        putBigInt(buf, xor);
        schnorr.pack(buf);
        Map<BigInteger, BigInteger> coeff = lagrange.getCoefficients();
        for (NaorPinkasShare share : shares) {
            BigInteger c = coeff.get(share.getI());
            assert c != null;
            putBigInt(buf, c);
        }
        for (NaorPinkasShare share : shares) {
            share.pack(buf);
        }
    }

    /**
     * Unpacks a message from the given byte buffer.
     * 
     * @param buf A byte buffer.
     * @return Naor-pinkas message.
     */
    public static NaorPinkasMessage unpack(ByteBuffer buf) {
        int t = buf.getInt();
        BigInteger r = getBigInt(buf),
                   xor = getBigInt(buf);
        SchnorrGroup schnorr = SchnorrGroup.unpack(buf);
        ImmutableList.Builder<BigInteger> coeffList = ImmutableList.builder();
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        for (int i = 0; i < t; ++i) {
            coeffList.add(getBigInt(buf));
        };
        for (int i = 0; i < t; ++i) {
            shares.add(NaorPinkasShare.unpack(t, r, schnorr, buf));
        }
        Map<BigInteger, BigInteger> coeffMap = 
                MapUtils.zip(NaorPinkasShare.getXsFromShares(shares.build()), coeffList.build());
        LagrangeInterpolation<BigInteger> lagrange = 
                new LagrangeInterpolation<BigInteger>(schnorr.getFieldModQ(), coeffMap);
        return new NaorPinkasMessage(t, r, xor, schnorr, 
                lagrange, shares.build());
    }
    
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        NaorPinkasMessage other = (NaorPinkasMessage)other_;
        return t == other.t
            && r.equals(other.r)
            && xor.equals(other.xor)
            && schnorr.equals(other.schnorr)
            && lagrange.equals(other.lagrange)
            && shares.equals(other.shares);
    }
}
