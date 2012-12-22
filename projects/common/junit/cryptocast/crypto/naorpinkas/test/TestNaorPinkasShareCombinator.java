package cryptocast.crypto.naorpinkas.test;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;

public class TestNaorPinkasShareCombinator {
    SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
    Field<BigInteger> modQ = schnorr.getFieldModQ(),
                      modP = schnorr.getFieldModP();
    NaorPinkasShareCombinator combi = new NaorPinkasShareCombinator();

    @Test
    public void canRestoreSecretFromValidShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        BigInteger r = BigInteger.valueOf(11111),
                   grp0 = schnorr.getPowerOfG(r.multiply(poly.evaluate(modQ.zero())));
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        shares.add(makeShare(poly, r, 44411));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build());
        assertTrue(actualGRP0.isPresent());
        assertEquals(grp0, actualGRP0.get());
    }
    
    @Test
    public void cannotRestoreSecretFromTooFewShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build());
        assertFalse(actualGRP0.isPresent());
    }
    
    @Test
    public void cannotRestoreSecretFromRedundantShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NaorPinkasShare> shares = ImmutableList.builder();
        shares.add(makeShare(poly, r, 22233333));
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build());
        assertFalse(actualGRP0.isPresent());
    }
    
    private NaorPinkasShare makeShare(Polynomial<BigInteger> poly, BigInteger r, int xi) {
        BigInteger x = BigInteger.valueOf(xi);
        return new NaorPinkasShare(poly.getDegree(), r, x, 
                                   schnorr.getPowerOfG(r.multiply(poly.evaluate(x))),
                                   schnorr);
    }

    private Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        BigInteger[] coeff = new BigInteger[coefficients.length];
        for (int i = 0; i < coeff.length; ++i) {
            coeff[i] = BigInteger.valueOf(coefficients[i]);
        }
        return new Polynomial<BigInteger>(field, coeff);
    }
}
