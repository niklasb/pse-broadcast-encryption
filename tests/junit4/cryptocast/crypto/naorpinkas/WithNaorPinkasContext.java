package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;

public class WithNaorPinkasContext {
    protected SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
    protected IntegersModuloPrime modQ = schnorr.getFieldModOrder(),
                                  modP = schnorr.getFieldModP();

    protected NaorPinkasShare<BigInteger, SchnorrGroup> makeShare(
                       Polynomial<BigInteger> poly, BigInteger r, int xi) {
        BigInteger x = BigInteger.valueOf(xi);
        return new NaorPinkasShare<BigInteger, SchnorrGroup>(
                x, schnorr.getPowerOfG(r.multiply(poly.evaluate(x))), schnorr);
    }

    protected Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        ImmutableList.Builder<BigInteger> coeff = ImmutableList.builder();
        for (int i = 0; i < coefficients.length; ++i) {
            coeff.add(BigInteger.valueOf(coefficients[i]));
        }
        return new Polynomial<BigInteger>(field, coeff.build());
    }
    
    protected NaorPinkasShareCombinator<BigInteger, SchnorrGroup>
                      makeCombinator(Polynomial<BigInteger> poly) {
        return new NaorPinkasShareCombinator<BigInteger, SchnorrGroup>(poly.getDegree(), schnorr);
    }
}
