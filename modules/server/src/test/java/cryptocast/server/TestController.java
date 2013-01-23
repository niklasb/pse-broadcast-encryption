package cryptocast.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestController {
    @Mock private File databaseFile;
    @Mock private File keyFile;
    private SocketAddress socketAddress;    
    private Controller sut;

//    @Before
//    public void setUp() throws IOException, ClassNotFoundException {
//        MockitoAnnotations.initMocks(this);
//        socketAddress = new InetSocketAddress(21337);
//        sut = Controller.start(databaseFile, socketAddress, 15);
//    }
}
