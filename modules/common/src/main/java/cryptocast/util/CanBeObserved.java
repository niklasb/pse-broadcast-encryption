package cryptocast.util;

import java.util.Observer;

/** Describes a class that can be observed. Can be used instead of the {@link
 * Observable} base classes in cases where we can't use it because we inherit
 * from a different class.
 */
public interface CanBeObserved {
    /** Adds an observer to the class.
     * @param o The new observer */
    public void addObserver(Observer o);
    /** Removes an observer from the class.
     * @param o The old observer */
    public void deleteObserver(Observer o);
}
