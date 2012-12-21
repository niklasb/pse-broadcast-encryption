package cryptocast.crypto.naorpinkas.test;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
import cryptocast.util.ByteUtils;

public class TestNaorPinkasShare {
	@Test
	public void packAndUnpackWorks() {
	    int t = 3;
	    ModularExponentiationGroup group = ModularExponentiationGroup.getP1024Q160();
	    // use large numbers to verify that getMaxSpace() works
	    BigInteger p = group.getP(),
	               r = p.subtract(BigInteger.valueOf(1)),
	               i = p.subtract(BigInteger.valueOf(2)),
	               grpi = p.subtract(BigInteger.valueOf(3));
	    NaorPinkasShare expected = new NaorPinkasShare(t, r, i, grpi, group);
		byte[] packed = ByteUtils.pack(expected);
		NaorPinkasShare actual = NaorPinkasShare.unpack(t, r, group, 
		        ByteUtils.startUnpack(packed));
		assertEquals(expected, actual);
	}
}
