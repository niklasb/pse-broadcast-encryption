package cryptocast.crypto.naorpinkas.test;

import java.math.BigInteger;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;

public class WithNaorPinkasContext {
    protected SchnorrGroup schnorr = SchnorrGroup.getP1024Q160();
    protected Field<BigInteger> modQ = schnorr.getFieldModQ(),
                                modP = schnorr.getFieldModP();
    protected NaorPinkasShareCombinator combi = new NaorPinkasShareCombinator();

    protected NaorPinkasShare makeShare(Polynomial<BigInteger> poly, BigInteger r, int xi) {
        BigInteger x = BigInteger.valueOf(xi);
        return new NaorPinkasShare(poly.getDegree(), r, x, 
                                   schnorr.getPowerOfG(r.multiply(poly.evaluate(x))),
                                   schnorr);
    }

    protected Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        BigInteger[] coeff = new BigInteger[coefficients.length];
        for (int i = 0; i < coeff.length; ++i) {
            coeff[i] = BigInteger.valueOf(coefficients[i]);
        }
        return new Polynomial<BigInteger>(field, coeff);
    }
}
