package cryptocast.util;

import java.io.File;

/**
 * Provides utility methods for file paths.
 */
public class FileUtils {
    /**
     * Expands the given tilde-path (<code>~/path/to/something</code>) into an absolute path.
     *
     * @param path The given path.
     * @return The absolute path.
     */
    public static File expandPath(String path) {
        if (path.startsWith("~/") || path.startsWith("~" + File.separator)) {
            path = System.getProperty("user.home") + path.substring(1);
        }
        return new File(path);
    }
}
