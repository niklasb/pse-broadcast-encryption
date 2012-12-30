package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import cryptocast.crypto.*;
import cryptocast.util.ByteUtils;

public class TestNaorPinkasShare {
	@Test
	public void packAndUnpackWorks() {
	    int t = 3;
	    SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
	    // use large numbers to verify that getMaxSpace() works
	    BigInteger p = schnorr.getP(),
	               r = p.subtract(BigInteger.valueOf(1)),
	               i = p.subtract(BigInteger.valueOf(2)),
	               grpi = p.subtract(BigInteger.valueOf(3));
	    NaorPinkasShare expected = new NaorPinkasShare(t, r, i, grpi, schnorr);
		byte[] packed = ByteUtils.pack(expected);
		NaorPinkasShare actual = NaorPinkasShare.unpack(t, r, schnorr, 
		        ByteUtils.startUnpack(packed));
		assertEquals(expected, actual);
	}
}
