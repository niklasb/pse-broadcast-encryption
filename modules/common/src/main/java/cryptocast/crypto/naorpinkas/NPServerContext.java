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

/**
 * Represents the state of a NP server
 */
public class NPServerContext<T, G extends CyclicGroupOfPrimeOrder<T>>
                                          implements Serializable {
    private static final long serialVersionUID = -3094894850650540736L;

    private static final Logger log = LoggerFactory
            .getLogger(NPServerContext.class);

    private int t;
    private G group;
    private Generator<NPKey<T, G>> keyGen;
    private Polynomial<BigInteger> poly;
    private LagrangeInterpolation<BigInteger> lagrange;

    private static SecureRandom rnd = new SecureRandom();

    public NPServerContext(int t, G group,
                           Generator<NPKey<T, G>> keyGen,
                           Polynomial<BigInteger> poly,
                           LagrangeInterpolation<BigInteger> lagrange) {
        this.t = t;
        this.group = group;
        this.keyGen = keyGen;
        this.poly = poly;
        this.lagrange = lagrange;
    }

    public LagrangeInterpolation<BigInteger> getLagrange() { return lagrange; }
    public G getGroup() { return group; }
    public Generator<NPKey<T, G>> getKeyGen() { return keyGen; }
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
    public static <T, G extends CyclicGroupOfPrimeOrder<T>>
                     NPServerContext<T, G> generate(int t, G group) {
        Field<BigInteger> modQ = group.getFieldModOrder();
        log.debug("Generating random polynomial");
        long start = System.currentTimeMillis();
        Polynomial<BigInteger> poly =
                Polynomial.createRandomPolynomial(rnd, modQ, t + 1);
        log.debug("Took {} ms", System.currentTimeMillis() - start);
        log.debug("Setting up dummy keys");
        start = System.currentTimeMillis();
        Generator<NPKey<T, G>> keyGen =
                new OptimisticGenerator<NPKey<T, G>>(
                        new NPKeyGenerator<T, G>(rnd, group, poly));
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
        return new NPServerContext<T, G>(t, group, keyGen, poly, lagrange);
    }
}
