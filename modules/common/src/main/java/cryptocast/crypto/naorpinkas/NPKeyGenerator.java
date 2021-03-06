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
 * A class generating NP keys.
 */
public class NPKeyGenerator<T, G extends CyclicGroupOfPrimeOrder<T>>
                    extends Generator<NPKey<T, G>>
                    implements Serializable {
    private static final long serialVersionUID = 2925906243884263202L;

    private SecureRandom rnd;
    private G group;
    private Field<BigInteger> modQ;
    private Polynomial<BigInteger> poly;
    private Map<NPIdentity, NPKey<T, G>> keyByIdentity =
               Maps.newHashMap();

    /**
     * Creates a new instance of NaorPinkasKeyGenerator with the given parameters.
     *
     * @param t The degree of the polynomial.
     * @param rnd The secure random number generator.
     * @param group The NP group.
     * @param poly The polynomial.
     */
    public NPKeyGenerator(SecureRandom rnd,
                                  G group,
                                  Polynomial<BigInteger> poly) {
        assert poly.getField().equals(group.getFieldModOrder());
        this.rnd = rnd;
        this.group = group;
        this.poly = poly;
        this.modQ = group.getFieldModOrder();
    }

    @Override
    public NPKey<T, G> get(int i) {
        BigInteger x = modQ.randomElement(rnd),
                   px = poly.evaluate(x);
        NPKey<T, G> key =
                new NPKey<T, G>(x, px, group);
        addKey(key);
        return key;
    }

    @Override
    public ImmutableList<NPKey<T, G>> getRange(int a, int b) {
        ImmutableList.Builder<BigInteger> xsBuilder = ImmutableList.builder();
        int len = b - a;
        for (int i = 0; i < len; ++i) {
            xsBuilder.add(poly.getField().randomElement(rnd));
        }
        ImmutableList<BigInteger>
            xs = xsBuilder.build(),
            ys = new PolynomialMultiEvaluation(xs).evaluate(poly);
        ImmutableList.Builder<NPKey<T, G>> keys =
                ImmutableList.builder();
        for (int i = 0; i < len; ++i) {
            NPKey<T, G> key =
                    new NPKey<T, G>(xs.get(i), ys.get(i), group);
            keys.add(key);
            addKey(key);
        }
        return keys.build();
    }

    /**
     * @param id An identity.
     * @return the key for the given identity.
     */
    public Optional<NPKey<T, G>> getKey(NPIdentity id) {
        return Optional.fromNullable(keyByIdentity.get(id));
    }

    private void addKey(NPKey<T, G> key) {
        keyByIdentity.put(key.getIdentity(), key);
    }
}
