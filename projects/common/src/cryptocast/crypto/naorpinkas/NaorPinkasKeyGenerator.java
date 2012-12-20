package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

import cryptocast.crypto.*;
import cryptocast.util.Generator;

public class NaorPinkasKeyGenerator extends Generator<NaorPinkasPersonalKey> {
    private int t;
    private SecureRandom rnd;
    private ModularExponentiationGroup group;
    private Polynomial<BigInteger> poly;
    private Map<NaorPinkasIdentity, NaorPinkasPersonalKey> keyByIdentity =
               new HashMap<NaorPinkasIdentity, NaorPinkasPersonalKey>();
    
    public NaorPinkasKeyGenerator(int t,
                                  SecureRandom rnd, 
                                  ModularExponentiationGroup group, 
                                  Polynomial<BigInteger> poly) {
        this.t = t;
        this.rnd = rnd;
        this.group = group;
        this.poly = poly;
    }

    @Override
    public NaorPinkasPersonalKey get(int i) {
        BigInteger x = poly.getField().randomElement(rnd),
                   y = poly.evaluate(x);
        NaorPinkasPersonalKey key = new NaorPinkasPersonalKey(t, x, y, group);
        addKey(key);
        return key;
    }

    @Override
    public NaorPinkasPersonalKey[] getRange(int a, int b) {
        BigInteger[] xs = new BigInteger[b - a];
        int len = xs.length;
        for (int i = 0; i < len; ++i) {
            xs[i] = poly.getField().randomElement(rnd);
        }
        BigInteger[] ys = poly.evaluateMulti(xs);
        NaorPinkasPersonalKey[] keys = new NaorPinkasPersonalKey[len];
        for (int i = 0; i < len; ++i) {
            keys[i] = new NaorPinkasPersonalKey(t, xs[i], ys[i], group);
            addKey(keys[i]);
        }
        return keys;
    }
    
    public Optional<NaorPinkasPersonalKey> getKey(NaorPinkasIdentity id) {
        return Optional.fromNullable(keyByIdentity.get(id));
    }
    
    private void addKey(NaorPinkasPersonalKey key) {
        keyByIdentity.put(key.getIdentity(), key);
    }
}