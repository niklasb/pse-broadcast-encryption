package cryptocast.client.test;

import cryptocast.client.MainActivity;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TestMainActivity {
    MainActivity sut;
    
    @Before
    public void setUp() throws Exception {
      sut = new MainActivity();
    }

    @Test
    public void serverNameSaving() {
        String expected = "serverName";
        sut.storeServerName(expected);
        String actual = sut.loadServerName();
        assertEquals(expected, actual);
    }
}
