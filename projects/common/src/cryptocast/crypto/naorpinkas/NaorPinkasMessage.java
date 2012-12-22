package cryptocast.crypto.naorpinkas;

import static cryptocast.util.ByteUtils.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;
import cryptocast.util.Packable;

public class NaorPinkasMessage implements Packable {
    private BigInteger xor;
    private BigInteger r;
    private ImmutableList<NaorPinkasShare> shares;
    private SchnorrGroup schnorr;
    int t;

    public NaorPinkasMessage(int t, BigInteger r, BigInteger xor, 
                             SchnorrGroup schnorr,
                             ImmutableList<NaorPinkasShare> shares) {
        this.t = t;
        this.r = r;
        this.xor = xor;
        this.schnorr = schnorr;
        this.shares = shares;
    }

    public BigInteger getXor() { return xor; }
    public BigInteger getR() { return r; }
    public ImmutableList<NaorPinkasShare> getShares() { return shares; }

    public int getMaxSpace() {
        return 4 + 2 * schnorr.getMaxNumberSpace() 
                 + schnorr.getMaxSpace() 
                 + shares.size() * shares.get(0).getMaxSpace();
    }

    public void pack(ByteBuffer buf) {
        buf.putInt(t);
        putBigInt(buf, r);
        putBigInt(buf, xor);
        schnorr.pack(buf);
        for (NaorPinkasShare share : shares) {
            share.pack(buf);
        }
    }

    public static NaorPinkasMessage unpack(ByteBuffer buf) {
        int t = buf.getInt();
        BigInteger r = getBigInt(buf),
                   xor = getBigInt(buf);
        SchnorrGroup schnorr = SchnorrGroup.unpack(buf);
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        for (int i = 0; i < t; ++i) {
            shares.add(NaorPinkasShare.unpack(t, r, schnorr, buf));
        }
        return new NaorPinkasMessage(t, r, xor, schnorr, shares.build());
    }
    
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        NaorPinkasMessage other = (NaorPinkasMessage)other_;
        return t == other.t
            && r.equals(other.r)
            && xor.equals(other.xor)
            && schnorr.equals(other.schnorr)
            && shares.equals(other.shares);
    }
}
