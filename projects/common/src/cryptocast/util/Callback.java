package cryptocast.util;

public interface Callback<T> {
    public void handle(T object);
}
