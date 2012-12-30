package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;
import cryptocast.util.ByteUtils;

public class TestNaorPinkasMessage extends WithNaorPinkasContext {
    @Test
    public void packAndUnpackWorks() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 6, 123, 22 });
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        BigInteger r = BigInteger.valueOf(111111),
                   xor = BigInteger.valueOf(222222);
        shares.add(makeShare(poly, r, 123));
        shares.add(makeShare(poly, r, 123123123));
        NaorPinkasMessage expected = 
                new NaorPinkasMessage(poly.getDegree(), r, xor, schnorr, shares.build());
        byte[] packed = ByteUtils.pack(expected);
        assertEquals(expected, 
                     NaorPinkasMessage.unpack(ByteUtils.startUnpack(packed)));
    }
    
    @Test
    public void canPackEmptyMessage() {
        BigInteger r = BigInteger.valueOf(111111),
                   xor = BigInteger.valueOf(222222);
        NaorPinkasMessage expected = 
                new NaorPinkasMessage(0, r, xor, schnorr,
                        ImmutableList.<NaorPinkasShare>builder().build());
        byte[] packed = ByteUtils.pack(expected);
        assertEquals(expected, 
                     NaorPinkasMessage.unpack(ByteUtils.startUnpack(packed)));
    }
}
