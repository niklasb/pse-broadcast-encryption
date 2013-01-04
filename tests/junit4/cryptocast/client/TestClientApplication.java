package cryptocast.client;

import java.io.File;
import java.net.InetSocketAddress;

import org.junit.*;
import org.junit.runner.RunWith;

import com.google.common.base.Optional;

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
        InetSocketAddress addr = new InetSocketAddress("foo", 1234);
        sut.getServerHistory().addServer(addr, new File("bar"));
        sut.setHostnameInput("foobar");
        sut.setPortInput("-333333333333");
        sut.saveState();
        sut = new ClientApplication();
        sut.onCreate();
        assertEquals("foobar", sut.getHostnameInput());
        assertEquals("-333333333333", sut.getPortInput());
        assertEquals(Optional.of(new File("bar")), 
                sut.getServerHistory().getKeyFile(addr));
    }
}
