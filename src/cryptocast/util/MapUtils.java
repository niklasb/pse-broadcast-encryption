package cryptocast.util;

import java.util.Iterator;

import com.google.common.collect.ImmutableMap;

public class MapUtils {
    public static <T, U> ImmutableMap<T, U> zip(Iterable<T> keys, Iterable<U> values) {
        ImmutableMap.Builder<T, U> map = ImmutableMap.builder();
        Iterator<U> valueIter = values.iterator();
        for (T key : keys) {
            map.put(key, valueIter.next());
        }
        return map.build();
    }
}
