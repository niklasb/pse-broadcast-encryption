package cryptocast.crypto;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.EllipticCurve.Point;

/**
 * Represents a cyclic group $(G, \otimes)$ of prime order $q$ over a subset 
 * $G$ of the values of type T, generated by $g$.
 * Let $e$ denote the identity element of $G$ and $x^{-1}$ the inverse of $x$.
 * @param <T> The values we work on.
 */
public abstract class CyclicGroupOfPrimeOrder<T> {
    private IntegersModuloPrime modQ;
    private T g;
    private BigInteger q;
    
    protected CyclicGroupOfPrimeOrder(T g, BigInteger q) {
        this.g = g;
        this.q = q;
        this.modQ = new IntegersModuloPrime(q);
    }
    
    /**
     * @return The generator $g$ of the group
     */
    public T getGenerator() {
        return g;
    }
    
    /**
     * @return $g^k$, where $g$ is the generator of the group
     */
    public T getPowerOfG(BigInteger k) {
        return pow(getGenerator(), k);
    }
    
    /**
     * @return The order $q$ of the group
     */
    public BigInteger getOrder() {
        return q;
    }
    
    /**
     * @return The field of integers modulo the order $q$ of the group
     */
    public IntegersModuloPrime getFieldModOrder() {
        return modQ;
    }
    
    /**
     * @param a The first operand
     * @param b The second operand 
     * @return $a \otimes b$ 
     */
    public abstract T combine(T a, T b);
    
    
    /**
     * @param a The element to combine with itself
     * @return $a \otimes a$
     */
    public T twice(T a) {
        return combine(a, a);
    }
    
    /**
     * Computes $a^k = \bigotimes_{i=1}^{k} a$ using a simple
     * square-multiply algorithm.
     * 
     * @param a The base
     * @param k The number of combinations of a with itself
     * @return $a^k = \bigotimes_{i=1}^{k} a$ 
     */
    public T pow(T a, BigInteger k) {
        T q = identity();
        for (int i = 0, len = k.bitLength(); i < len; i++) {
            if (k.testBit(i)) {
                q = combine(q, a);
            }
            a = twice(a);
        }
        return q;
    }

    /**
     * Multi-exponentation. Uses {@link multiexpShamir} by default
     * with $k = 5$.
     * 
     * @param bases The bases $b_i$
     * @param exponents The exponents $e_i$
     * @return $\bigotimes_{i=1}^n b_i^{e_i}$, where $n$
     *         is the size of the input lists
     */
    public T multiexp(List<T> bases, List<BigInteger> exponents) {
        return multiexpShamir(bases, exponents, 5);
    }

    /**
     * Implements multi-exponentation by applying Shamir's trick
     * to chunks of a given size.
     * Uses {@link List.subList}, so you'd better give it 
     * {@link ImmutableList}s.
     * 
     * @param bases The items $b_i$
     * @param exponents The exponents $e_i$
     * @param $k$ The chunk size
     * @return $\bigotimes_{i=1}^n b_i^{e_i}$, where $n$
     *         is the size of the input lists
     */
    public T multiexpShamir(List<T> bases, List<BigInteger> exponents, int k) {
        T res = identity();
        int len = bases.size();
        res = combine(res, shamir(bases.subList(0, len % k), 
                                  exponents.subList(0, len % k)));
        for (int i = len % k; i < len; i += k) {
            res = combine(res, shamir(bases.subList(i, i + k), 
                                      exponents.subList(i, i + k)));
        }
        return res;
    }
    
    // Implements Shamir's trick for multiple variables.
    // Needs space exponential in the length of the input lists.
    private T shamir(List<T> bases, List<BigInteger> exponents) {
        int len = bases.size();
        assert len == exponents.size();
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (int i = 0; i < (1<<len); ++i) {
            T sum = identity();
            for (int j = 0; j < len; ++j) {
                if ((i & j) == 0) { continue; }
                sum = combine(sum, bases.get(j));
            }
            builder.add(sum);
        }
        ImmutableList<T> precomp = builder.build();
        int m = 0;
        for (BigInteger e : exponents) { 
            m = Math.max(m, e.bitLength()); 
        }
        T r = identity();
        for (int i = m - 1; i >= 0; --i) {
            r = twice(r);
            int bits = 0;
            for (int j = 0; j < len; ++j) {
                if (exponents.get(j).testBit(i)) {
                    bits |= (1 << j);
                }
            }
            r = combine(r, precomp.get(bits));
        }
        return r;
    }
    
    /**
     * Implements Shamir's trick for two variables.
     * 
     * @param a
     * @param k
     * @param b
     * @param l
     * @return The value $a^k \otimes b^l$.
     */
    public T shamir(T a, BigInteger k, T b, BigInteger l) {
        T z = combine(a, b);
        T r = identity();
        int m = Math.max(k.bitLength(), l.bitLength());
        for (int i = m - 1; i >= 0; --i) {
            r = twice(r);
            if (k.testBit(i)) {
                if (l.testBit(i)) {
                    r = combine(r, z);
                } else {
                    r = combine(r, a);
                }
            } else if (l.testBit(i)) {
                r = combine(r, b);
            }
        }
        return r;
    }

    /**
     * @param a
     * @return The unique inverse element $a^{-1} \in G$, such that
     *         $a \otimes a^{-1} = e$
     */
    public abstract T invert(T a);
    
    /**
     * @return $e$, the identity element of $G$
     */
    public abstract T identity();
}
