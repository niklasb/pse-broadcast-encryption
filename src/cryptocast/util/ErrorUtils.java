package cryptocast.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorUtils {
    private static final Logger log = LoggerFactory.getLogger(ErrorUtils.class);
    
    public static void cannotHappen(Throwable e) throws AssertionError {
        log.error("An exception occured of which we stated it cannot occur!", e);
        throwWithCause(new AssertionError("Impossible exception occured"), e);
    }
    
    public static void cannotHappen() throws AssertionError {
        throw new AssertionError("Can never be reached. Or can it?");
    }
    
    public static <T extends Throwable> void throwWithCause(T e, Throwable cause) throws T {
        e.initCause(cause);
        throw e;
    }
}
