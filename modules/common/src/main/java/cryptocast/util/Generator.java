package cryptocast.util;

import com.google.common.collect.ImmutableList;

/**
 * An abstract entity that models a function over the intergers ($f: \mathbb{Z}
 * \to T$)
 *
 * @param <T> The object type for range generator.
 */
public abstract class Generator<T> {
    /** @return the value $f(i)$ */
    public abstract T get(int i);

    /**
     * Given an interval $[a, b)$ of integers, this functions will compute the
     * values $(f(a), f(a + 1), ..., f(b - 1))$.
     *
     * @param a The left interval bound (inclusive).
     * @param b The right interval bound (exclusive).
     * @return a {@link List} of values representing the generated range.
     */
    public ImmutableList<T> getRange(int a, int b) {
        ImmutableList.Builder<T> values = ImmutableList.builder();
        for (int i = 0, len = b - a; i < len; ++i) {
            values.add(get(a + i));
        }
        return values.build();
    }
}
