package cryptocast.util;

import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * An optimistic generator.
 * @param <T> the type of the generator object.
 */
public class OptimisticGenerator<T> extends Generator<T> implements Serializable {
    private static final Logger log = LoggerFactory
            .getLogger(OptimisticGenerator.class);
    
    private static final long serialVersionUID = -544402880183253672L;
    
    ArrayList<T> values = new ArrayList<T>();
    private Generator<T> inner;

    /**
     * Creates an instance of this class.
     * 
     * @param inner The generator upon which this generator is based.
     */
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
        
        int a = values.size(), b = newSize;
        log.trace("Generating range [{}, {})", a, b);
        long start = System.currentTimeMillis();
        for (T x : inner.getRange(a, b)) {
            values.add(x);
        }
        log.trace("Took {} ms", System.currentTimeMillis() - start);
    }
}