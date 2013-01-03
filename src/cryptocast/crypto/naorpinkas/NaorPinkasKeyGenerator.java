package cryptocast.crypto.naorpinkas;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;
import cryptocast.util.Generator;

public class NaorPinkasKeyGenerator extends Generator<NaorPinkasPersonalKey> 
                                    implements Serializable {
    private static final long serialVersionUID = 2925906243884263202L;
    
    private int t;
    private SecureRandom rnd;
    private SchnorrGroup schnorr;
    private Polynomial<BigInteger> poly;
    private Map<NaorPinkasIdentity, NaorPinkasPersonalKey> keyByIdentity =
               new HashMap<NaorPinkasIdentity, NaorPinkasPersonalKey>();
    
    public NaorPinkasKeyGenerator(int t,
                                  SecureRandom rnd, 
                                  SchnorrGroup schnorr, 
                                  Polynomial<BigInteger> poly) {
        this.t = t;
        this.rnd = rnd;
        this.schnorr = schnorr;
        this.poly = poly;
    }

    @Override
    public NaorPinkasPersonalKey get(int i) {
        BigInteger x = poly.getField().randomElement(rnd),
                   y = poly.evaluate(x);
        NaorPinkasPersonalKey key = new NaorPinkasPersonalKey(t, x, y, schnorr);
        addKey(key);
        return key;
    }

    @Override
    public ImmutableList<NaorPinkasPersonalKey> getRange(int a, int b) {
        ImmutableList.Builder<BigInteger> xsBuilder = ImmutableList.builder();
        int len = b - a;
        for (int i = 0; i < len; ++i) {
            xsBuilder.add(poly.getField().randomElement(rnd));
        }
        ImmutableList<BigInteger> xs = xsBuilder.build();
        ImmutableList<BigInteger> ys = poly.evaluateMulti(xs);
        ImmutableList.Builder<NaorPinkasPersonalKey> keys = ImmutableList.builder();
        for (int i = 0; i < len; ++i) {
            NaorPinkasPersonalKey key = new NaorPinkasPersonalKey(t, xs.get(i), ys.get(i), schnorr);
            keys.add(key);
            addKey(key);
        }
        return keys.build();
    }
    
    public Optional<NaorPinkasPersonalKey> getKey(NaorPinkasIdentity id) {
        return Optional.fromNullable(keyByIdentity.get(id));
    }
    
    private void addKey(NaorPinkasPersonalKey key) {
        keyByIdentity.put(key.getIdentity(), key);
    }
}
