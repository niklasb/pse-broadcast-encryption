package cryptocast.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides several error handing utility methods.
 */
public class ErrorUtils {
    private static final Logger log = LoggerFactory.getLogger(ErrorUtils.class);
    
    /**
     * Throws an exception/error when an exception occurs which shouldn't have happened.
     * 
     * @param e The exception that shouldn't have happened.
     * @throws AssertionError
     */
    public static void cannotHappen(Throwable e) throws AssertionError {
        log.error("An exception occured of which we stated it cannot occur!", e);
        throw new RuntimeException("Impossible exception occured", e);
    }
    
    /**
     * Throws an exception/error when reached.
     * 
     * @throws AssertionError
     */
    public static void cannotHappen() throws AssertionError {
        throw new AssertionError("Can never be reached. Or can it?");
    }
}
