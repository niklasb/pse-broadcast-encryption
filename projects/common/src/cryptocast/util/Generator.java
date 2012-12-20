package cryptocast.util;

public abstract class Generator<T> {
    public abstract T get(int i);
 
    public T[] getRange(int a, int b) {
        @SuppressWarnings("unchecked")
        T[] values = (T[])new Object[b-a];
        for (int i = 0, len = b - a; i < len; ++i) {
            values[i] = get(a + i);
        }
        return values;
    }
}
