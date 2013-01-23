package cryptocast.util;

import java.util.Observer;

public interface CanBeObserved {
    public void addObserver(Observer o);
    public void deleteObserver(Observer o);
}
