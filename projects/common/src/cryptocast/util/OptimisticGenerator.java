package cryptocast.util;

import java.io.Serializable;
import java.util.ArrayList;

public class OptimisticGenerator<T> extends Generator<T> implements Serializable {
    private static final long serialVersionUID = -544402880183253672L;
    
    ArrayList<T> values = new ArrayList<T>();
    private Generator<T> inner;

    public OptimisticGenerator(Generator<T> inner) {
        this.inner = inner;
    }

    @Override
    public T get(int i) {
        extend(i);
        return values.get(i);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public T[] getRange(int a, int b) {
        if (a == b) { 
            return (T[])new Object[0];
        }
        extend(b);
        T[] result = (T[])new Object[b - a];
        for (int i = 0; i < b - a; ++i) {
            result[i] = values.get(a + i);
        }
        return result;
    }
    
    private void extend(int i) {
        if (i < values.size()) {
            return;
        }
        int newSize = 1;
        // find next power of two
        while (newSize <= i) {
            newSize <<= 1;
        }
        for (T x : inner.getRange(values.size(), newSize)) {
            values.add(x);
        }
    }
}
