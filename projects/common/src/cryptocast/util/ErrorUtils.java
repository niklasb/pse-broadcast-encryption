package cryptocast.util;

import static com.google.common.base.Throwables.propagate;

public class ErrorUtils {
    public static void cannotHappen(Throwable e) throws RuntimeException {
        assert false;
        propagate(e);
    }
    
    public static void cannotHappen() throws AssertionError {
        throw new AssertionError("Can never be reached. Or can it?");
    }
}
