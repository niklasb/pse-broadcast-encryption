package cryptocast.util;

import java.io.File;

public class FileUtils {
    public static File expandPath(String path) {
        if (path.startsWith("~" + File.separator)) {
            path = System.getProperty("user.home") + path.substring(1);
        }
        return new File(path);
    }
}
