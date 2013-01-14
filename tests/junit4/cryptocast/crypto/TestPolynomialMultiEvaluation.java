package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestPolynomialMultiEvaluation {
    private Field<BigInteger> mod11 = new IntegersModuloPrime(BigInteger.valueOf(11));
    private static Random rnd = new Random();
    
    @Test
    public void canEvaluateModPoly() {
        Polynomial<BigInteger> poly = makePolynomial(mod11, new int[] { 1, 2, 3 });
        ImmutableList<BigInteger> xs = intArrayToBigIntList(new int[] { 0, 1, 2 });
        PolynomialMultiEvaluation eval = new PolynomialMultiEvaluation(xs);
        assertEquals(poly.evaluateMulti(xs), eval.evaluate(poly));
    }
    
    @Test
    public void smallN() {
        for (int n = 0; n < 10; ++n) {
            randomTest(100, n);
        }
    }
    
    @Test
    public void smallRandomTests() {
        for (int i = 0; i < 50; ++i) {
            randomTest(rnd.nextInt(20), rnd.nextInt(50));
        }
    }
    
    @Test
    public void largeRandomTest() {
        randomTest(500, 1000);
    }
    
    private void randomTest(int t, int n) {
        IntegersModuloPrime field = SchnorrGroup.getP1024Q160().getFieldModOrder();
        Polynomial<BigInteger> poly = Polynomial.createRandomPolynomial(rnd, field, t);
        ImmutableList.Builder<BigInteger> xs = ImmutableList.builder();
        for (int i = 0; i < n; ++i) {
            xs.add(field.randomElement(rnd));
        }
        assertEquals(poly.evaluateMulti(xs.build()), 
                new PolynomialMultiEvaluation(xs.build()).evaluate(poly));
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
