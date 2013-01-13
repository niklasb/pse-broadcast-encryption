package cryptocast.util;

/**
 * Defines a class as a callback-able.
 * @param <T> the type of the callback object for handling.
 */
public interface Callback<T> {
    /**
     * Handles a callback with the given object.
     * 
     * @param object The callback object for handling.
     */
    public void handle(T object);
}
