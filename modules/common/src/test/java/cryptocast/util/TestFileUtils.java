package cryptocast.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class TestFileUtils {
    @Test
    public void expandPath() {
        String home = System.getProperty("user.home");
        assertEquals(new File(home + "/test"), FileUtils.expandPath("~/test"));
        assertEquals(new File(home + "/test"), FileUtils.expandPath("~" + File.separator + "test"));
    }
}
