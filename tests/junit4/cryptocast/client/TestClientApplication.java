package cryptocast.client;

import java.io.File;

import android.widget.TextView;
import cryptocast.client.MainActivity;
import cryptocast.client.R;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(ClientTestRunner.class)
public class TestClientApplication {
    ClientApplication sut;

    @Before
    public void setUp() throws Exception {
      sut = new ClientApplication();
      sut.onCreate();
    }

    @Test
    public void savesAndReloadsState() {
        sut.getServerHistory().addServer("foo", new File("bar"));
        sut.setHostname("foobar");
        sut.saveState();
        sut = new ClientApplication();
        sut.onCreate();
        assertEquals("foobar", sut.getHostname());
        assertEquals(new File("bar"), 
                sut.getServerHistory().getServers().get("foo"));
    }
}
