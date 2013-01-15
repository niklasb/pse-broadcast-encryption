package cryptocast.crypto.naorpinkas;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import cryptocast.crypto.*;
import cryptocast.util.Generator;

/**
 * This class is for generatingnaor-pinkas keys.
 */
public class NaorPinkasKeyGenerator<T, G extends CyclicGroupOfPrimeOrder<T>> 
                    extends Generator<NaorPinkasPersonalKey<T, G>> 
                    implements Serializable {
    private static final long serialVersionUID = 2925906243884263202L;
    
    private SecureRandom rnd;
    private G group;
    private Field<BigInteger> modQ;
    private Polynomial<BigInteger> poly;
    private Map<NaorPinkasIdentity, NaorPinkasPersonalKey<T, G>> keyByIdentity =
               Maps.newHashMap();
    
    /**
     * Creates a new instance of NaorPinkasKeyGenerator with the given parameter.
     * 
     * @param t The degree of the polynomial.
     * @param rnd The secure random number generator.
     * @param group The NP group.
     * @param poly The polynomial.
     */
    public NaorPinkasKeyGenerator(SecureRandom rnd, 
                                  G group, 
                                  Polynomial<BigInteger> poly) {
        assert poly.getField().equals(group.getFieldModOrder());
        this.rnd = rnd;
        this.group = group;
        this.poly = poly;
        this.modQ = group.getFieldModOrder();
    }

    @Override
    public NaorPinkasPersonalKey<T, G> get(int i) {
        BigInteger x = modQ.randomElement(rnd),
                   px = poly.evaluate(x);
        NaorPinkasPersonalKey<T, G> key = 
                new NaorPinkasPersonalKey<T, G>(x, px, group);
        addKey(key);
        return key;
    }

    @Override
    public ImmutableList<NaorPinkasPersonalKey<T, G>> getRange(int a, int b) {
        ImmutableList.Builder<BigInteger> xsBuilder = ImmutableList.builder();
        int len = b - a;
        for (int i = 0; i < len; ++i) {
            xsBuilder.add(poly.getField().randomElement(rnd));
        }
        ImmutableList<BigInteger> 
            xs = xsBuilder.build(),
            ys = new PolynomialMultiEvaluation(xs).evaluate(poly);
        ImmutableList.Builder<NaorPinkasPersonalKey<T, G>> keys = 
                ImmutableList.builder();
        for (int i = 0; i < len; ++i) {
            NaorPinkasPersonalKey<T, G> key = 
                    new NaorPinkasPersonalKey<T, G>(xs.get(i), ys.get(i), group);
            keys.add(key);
            addKey(key);
        }
        return keys.build();
    }
    
    /**
     * @param id An identity.
     * @return the key for the given identity.
     */
    public Optional<NaorPinkasPersonalKey<T, G>> getKey(NaorPinkasIdentity id) {
        return Optional.fromNullable(keyByIdentity.get(id));
    }
    
    private void addKey(NaorPinkasPersonalKey<T, G> key) {
        keyByIdentity.put(key.getIdentity(), key);
    }
}
