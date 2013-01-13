package cryptocast.util;

import com.google.common.collect.ImmutableList;

/**
 * A range generator.
 * @param <T> The object type for range generator.
 */
public abstract class Generator<T> {
    public abstract T get(int i);
 
    /**
     * Generate a range for the given values.
     * 
     * @param a The the left range value.
     * @param b The right range value.
     * @return a List representing the generated range.
     */
    public ImmutableList<T> getRange(int a, int b) {
        ImmutableList.Builder<T> values = ImmutableList.builder();
        for (int i = 0, len = b - a; i < len; ++i) {
            values.add(get(a + i));
        }
        return values.build();
    }
}
