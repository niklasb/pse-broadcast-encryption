package cryptocast.crypto.naorpinkas;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;
import cryptocast.util.Generator;
import cryptocast.util.OptimisticGenerator;

public class NaorPinkasServerContext<T> implements Serializable {
    private static final long serialVersionUID = -3094894850650540736L;

    private static final Logger log = LoggerFactory
            .getLogger(NaorPinkasServerContext.class);
    
    private int t;
    private CyclicGroupOfPrimeOrder<T> group;
    private Generator<NaorPinkasPersonalKey<T>> keyGen;
    private Polynomial<BigInteger> poly;
    private LagrangeInterpolation<BigInteger> lagrange;

    private static SecureRandom rnd = new SecureRandom();
    
    public NaorPinkasServerContext(int t, CyclicGroupOfPrimeOrder<T> group, 
                                   Generator<NaorPinkasPersonalKey<T>> keyGen,
                                   Polynomial<BigInteger> poly,
                                   LagrangeInterpolation<BigInteger> lagrange) {
        this.t = t;
        this.group = group;
        this.keyGen = keyGen;
        this.poly = poly;
        this.lagrange = lagrange;
    }

    public LagrangeInterpolation<BigInteger> getLagrange() { return lagrange; }
    public CyclicGroupOfPrimeOrder<T> getGroup() { return group; }
    public Generator<NaorPinkasPersonalKey<T>> getKeyGen() { return keyGen; }
    public Polynomial<BigInteger> getPoly() { return poly; }
    
    /**
     * @return The degree of the polynomial.
     */
    public int getT() { return t; }

    /**
     * Generates a NP server context.
     * 
     * @param t The degree of the polynomial.
     * @param group The NP group.
     * @return Naor-pinkas server instance.
     */
    public static <T> NaorPinkasServerContext<T> generate(
                            int t, CyclicGroupOfPrimeOrder<T> group) {
        Field<BigInteger> modQ = group.getFieldModOrder();
        log.debug("Generating random polynomial");
        long start = System.currentTimeMillis();
        Polynomial<BigInteger> poly = 
                Polynomial.createRandomPolynomial(rnd, modQ, t + 1);
        log.debug("Took {} ms", System.currentTimeMillis() - start);
        log.debug("Setting up dummy keys");
        start = System.currentTimeMillis();
        Generator<NaorPinkasPersonalKey<T>> keyGen = 
                new OptimisticGenerator<NaorPinkasPersonalKey<T>>(
                        new NaorPinkasKeyGenerator<T>(
                                t, rnd, group, poly));
        ImmutableList.Builder<BigInteger> dummyXs = ImmutableList.builder();
        for (int i = 0; i < t; ++i) {
            dummyXs.add(keyGen.get(i).getIdentity().getI());
        }
        log.debug("Took {} ms", System.currentTimeMillis() - start);
        log.debug("Computing initial lagrange coefficients");
        start = System.currentTimeMillis();
        LagrangeInterpolation<BigInteger> lagrange = 
                LagrangeInterpolation.fromXs(modQ, dummyXs.build());
        log.debug("Took {} ms", System.currentTimeMillis() - start);
        return new NaorPinkasServerContext<T>(t, group, keyGen, poly, lagrange);
    }
}
