package cryptocast.util;

import java.io.File;

/**
 * Provides methods for file/io utility methods.
 */
public class FileUtils {
    /**
     * Expands the given tilde-path (~/path/to/something) into an absolute path.
     * 
     * @param path The given path.
     * @return The absolute path.
     */
    public static File expandPath(String path) {
        if (path.startsWith("~" + File.separator)) {
            path = System.getProperty("user.home") + path.substring(1);
        }
        return new File(path);
    }
}
