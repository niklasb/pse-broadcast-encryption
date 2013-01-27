package cryptocast.util;

import java.util.Iterator;

import com.google.common.collect.ImmutableMap;

/**
 * Provides map utility methods.
 */
public class MapUtils {
    /**
     * Build a map with the given keys and values.
     *
     * @param keys The keys of the map.
     * @param values The values of the map.
     * @return A map with the given keys and values.
     */
    public static <T, U> ImmutableMap<T, U> zip(Iterable<T> keys, Iterable<U> values) {
        ImmutableMap.Builder<T, U> map = ImmutableMap.builder();
        Iterator<U> valueIter = values.iterator();
        for (T key : keys) {
            map.put(key, valueIter.next());
        }
        return map.build();
    }
}
