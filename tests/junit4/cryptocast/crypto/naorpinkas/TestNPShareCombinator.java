package cryptocast.crypto.naorpinkas;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;

public class TestNPShareCombinator {
    private SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
    private IntegersModuloPrime modQ = schnorr.getFieldModOrder(),
                                modP = schnorr.getFieldModP();
    
    @Test
    public void canRestoreSecretFromValidShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        NPShareCombinator<BigInteger, SchnorrGroup> combi = makeCombinator(poly);
        BigInteger r = BigInteger.valueOf(11111),
                   grp0 = schnorr.getPowerOfG(modQ.multiply(r, poly.evaluate(modQ.zero())));
        ImmutableList.Builder<NPShare<BigInteger, SchnorrGroup>> shares =
                ImmutableList.builder();
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        shares.add(makeShare(poly, r, 44411));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build());
        assertTrue(actualGRP0.isPresent());
        assertEquals(grp0, actualGRP0.get());
    }
    
    @Test
    public void detectsRedundantShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        NPShareCombinator<BigInteger, SchnorrGroup> combi = makeCombinator(poly);
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NPShare<BigInteger, SchnorrGroup>> shares = 
                ImmutableList.builder();
        shares.add(makeShare(poly, r, 22233333));
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        assertTrue(combi.hasRedundantShares(shares.build()));
    }
    
    @Test
    public void detectsMissingShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        NPShareCombinator<BigInteger, SchnorrGroup> combi = makeCombinator(poly);
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NPShare<BigInteger, SchnorrGroup>> shares = 
                ImmutableList.builder();
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        assertTrue(combi.hasMissingShares(shares.build()));
    }
    
    @Test
    public void cannotRestoreSecretFromTooFewShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        NPShareCombinator<BigInteger, SchnorrGroup> combi = makeCombinator(poly);
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NPShare<BigInteger, SchnorrGroup>> shares = 
                ImmutableList.builder();
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build());
        assertFalse(actualGRP0.isPresent());
    }
    
    @Test
    public void cannotRestoreSecretFromRedundantShares() {
        Polynomial<BigInteger> poly = makePolynomial(modQ, new int[] { 2, 3, 7, 11 });
        NPShareCombinator<BigInteger, SchnorrGroup> combi = makeCombinator(poly);
        BigInteger r = BigInteger.valueOf(11111);
        ImmutableList.Builder<NPShare<BigInteger, SchnorrGroup>> shares = 
                ImmutableList.builder();
        shares.add(makeShare(poly, r, 22233333));
        shares.add(makeShare(poly, r, 123123));
        shares.add(makeShare(poly, r, 33123));
        shares.add(makeShare(poly, r, 22233333));
        Optional<BigInteger> actualGRP0 = combi.restore(shares.build());
        assertFalse(actualGRP0.isPresent());
    }

    private NPShare<BigInteger, SchnorrGroup> makeShare(
                       Polynomial<BigInteger> poly, BigInteger r, int xi) {
        BigInteger x = BigInteger.valueOf(xi);
        return new NPShare<BigInteger, SchnorrGroup>(
                x, schnorr.getPowerOfG(r.multiply(poly.evaluate(x))), schnorr);
    }

    private Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        ImmutableList.Builder<BigInteger> coeff = ImmutableList.builder();
        for (int i = 0; i < coefficients.length; ++i) {
            coeff.add(BigInteger.valueOf(coefficients[i]));
        }
        return new Polynomial<BigInteger>(field, coeff.build());
    }
    
    private NPShareCombinator<BigInteger, SchnorrGroup>
                      makeCombinator(Polynomial<BigInteger> poly) {
        return new NPShareCombinator<BigInteger, SchnorrGroup>(poly.getDegree(), schnorr);
    }
}
