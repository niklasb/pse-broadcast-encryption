package cryptocast.client.test;

import org.junit.runners.model.InitializationError;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class ClientTestRunner extends RobolectricTestRunner {
    public ClientTestRunner(Class<?> testClass) throws InitializationError {
        // use the client project root as the working directory for
        // out client tests
        super(testClass, "../client");
    }
}
