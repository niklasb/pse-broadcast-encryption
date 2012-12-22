package cryptocast.util;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

public class OptimisticGenerator<T> extends Generator<T> implements Serializable {
    private static final long serialVersionUID = -544402880183253672L;
    
    ArrayList<T> values = new ArrayList<T>();
    private Generator<T> inner;

    public OptimisticGenerator(Generator<T> inner) {
        this.inner = inner;
    }

    @Override
    public T get(int i) {
        extend(i + 1);
        return values.get(i);
    }
    
    @Override
    public ImmutableList<T> getRange(int a, int b) {
        if (a == b) { 
            return ImmutableList.<T>builder().build();
        }
        extend(b);
        ImmutableList.Builder<T> result = ImmutableList.builder();
        for (int i = 0; i < b - a; ++i) {
            result.add(values.get(a + i));
        }
        return result.build();
    }
    
    private void extend(int minLen) {
        if (minLen <= values.size()) {
            return;
        }
        int newSize = 1;
        // find next power of two
        while (newSize < minLen) {
            newSize <<= 1;
        }
        for (T x : inner.getRange(values.size(), newSize)) {
            values.add(x);
        }
    }
}
