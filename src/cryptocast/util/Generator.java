package cryptocast.util;

import com.google.common.collect.ImmutableList;

public abstract class Generator<T> {
    public abstract T get(int i);
 
    public ImmutableList<T> getRange(int a, int b) {
        ImmutableList.Builder<T> values = ImmutableList.builder();
        for (int i = 0, len = b - a; i < len; ++i) {
            values.add(get(a + i));
        }
        return values.build();
    }
}
