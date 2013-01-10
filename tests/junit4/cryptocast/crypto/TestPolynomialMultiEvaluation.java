package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestPolynomialMultiEvaluation {
    Field<BigInteger> mod11 = new IntegersModuloPrime(BigInteger.valueOf(11));
    
    @Test
    public void canEvaluateModPoly() {
        Polynomial<BigInteger> poly = makePolynomial(mod11, new int[] { 1, 2, 3 });
        ImmutableList<BigInteger> xs = intArrayToBigIntList(new int[] { 0, 1, 2 });
        PolynomialMultiEvaluation eval = new PolynomialMultiEvaluation(xs);
        assertEquals(poly.evaluateMulti(xs), eval.evaluate(poly));
    }
    
    private ImmutableList<BigInteger> intArrayToBigIntList(int[] xs) {
        ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
        for (int x : xs) {
            builder.add(BigInteger.valueOf(x));
        }
        return builder.build();
    }
    
    private Polynomial<BigInteger> makePolynomial(Field<BigInteger> field, int coefficients[]) {
        ImmutableList.Builder<BigInteger> coeff = ImmutableList.builder();
        for (int i = 0; i < coefficients.length; ++i) {
            coeff.add(BigInteger.valueOf(coefficients[i]));
        }
        return new Polynomial<BigInteger>(field, coeff.build());
    }
}
