package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;

public class TestNaorPinkasShareCombinator extends WithNaorPinkasContext {
    
    @Test
    public void canRestoreSecretFromValidShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        BigInteger r = BigInteger.valueOf(11111),
                   grp0 = schnorr.getPowerOfG(modQ.multiply(r, poly.evaluate(modQ.zero())));
        ImmutableList.Builder<NaorPinkasShare<BigInteger>> shares = ImmutableList.builder();
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        shares.add(makeShare(poly, r, 44411));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build(), schnorr);
        assertTrue(actualGRP0.isPresent());
        assertEquals(grp0, actualGRP0.get());
    }
    
    @Test
    public void detectsRedundantShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NaorPinkasShare<BigInteger>> shares = ImmutableList.builder();
        shares.add(makeShare(poly, r, 22233333));
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        assertTrue(combi.hasRedundantShares(shares.build()));
    }
    
    @Test
    public void detectsMissingShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NaorPinkasShare<BigInteger>> shares = ImmutableList.builder();
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        assertTrue(combi.hasMissingShares(shares.build()));
    }
    
    @Test
    public void cannotRestoreSecretFromTooFewShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NaorPinkasShare<BigInteger>> shares = ImmutableList.builder();
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build(), schnorr);
        assertFalse(actualGRP0.isPresent());
    }
    
    @Test
    public void cannotRestoreSecretFromRedundantShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NaorPinkasShare<BigInteger>> shares = ImmutableList.builder();
        shares.add(makeShare(poly, r, 22233333));
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build(), schnorr);
        assertFalse(actualGRP0.isPresent());
    }
}
