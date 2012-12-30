package cryptocast.testutils;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestUtils {
    public static Answer<Object> RETURNS_FIRST_ARGUMENT() {
        return new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
              return invocation.getArguments()[0];
            }
        };
    }
}

