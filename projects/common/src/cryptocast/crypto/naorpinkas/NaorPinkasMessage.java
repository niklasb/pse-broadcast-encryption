package cryptocast.crypto.naorpinkas;

import static cryptocast.util.ByteUtils.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;
import cryptocast.util.Packable;

public class NaorPinkasMessage implements Packable {
    private BigInteger xor;
    private BigInteger r;
    private ImmutableList<NaorPinkasShare> shares;
    private ModularExponentiationGroup group;
    int t;
    
    public NaorPinkasMessage(int t, BigInteger r, BigInteger xor, 
                             ModularExponentiationGroup group,
                             ImmutableList<NaorPinkasShare> shares) {
        this.t = t;
        this.r = r;
        this.xor = xor;
        this.group = group;
        this.shares = shares;
    }
    
    public BigInteger getXor() { return xor; }
    public BigInteger getR() { return xor; }
    public ImmutableList<NaorPinkasShare> getShares() { return shares; }
    
    public int getMaxSpace() {
        return 4 + 2 * group.getMaxNumberSpace() 
                 + group.getMaxSpace() 
                 + shares.size() * shares.get(0).getMaxSpace();
    }

    public void pack(ByteBuffer buf) {
        buf.putInt(t);
        putBigInt(buf, r);
        putBigInt(buf, xor);
        group.pack(buf);
        for (NaorPinkasShare share : shares) {
            share.pack(buf);
        }
    }
    
    public static NaorPinkasMessage unpack(ByteBuffer buf) {
        int t = buf.getInt();
        BigInteger r = getBigInt(buf),
                   xor = getBigInt(buf);
        ModularExponentiationGroup group = ModularExponentiationGroup.unpack(buf);
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        for (int i = 0; i < t; ++i) {
            shares.add(NaorPinkasShare.unpack(t, r, group, buf));
        }
        return new NaorPinkasMessage(t, r, xor, group, shares.build());
    }
}
