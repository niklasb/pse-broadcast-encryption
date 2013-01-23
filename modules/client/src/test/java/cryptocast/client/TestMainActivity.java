package cryptocast.client;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cryptocast.client.MainActivity;
import cryptocast.client.R;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(ClientTestRunner.class)
public class TestMainActivity {
    MainActivity sut;

    @Before
    public void setUp() throws Exception {
      sut = new MainActivity();
      sut.onCreate(null);
    }

    @Test
    public void savesAndRestoresHostname() {
        String hostname = "foo";
        TextView tv = (TextView) sut.findViewById(R.id.editHostname);
        tv.setText(hostname);
        sut.onPause();
        tv.setText("ASDASD");
        sut.onResume();
        assertEquals(hostname, tv.getText().toString());
    }
    
    @Test
    public void illegalHostnames() {
        assertFalse(sut.checkHostname(""));
        assertTrue(sut.checkHostname("google.de"));
        assertTrue(sut.checkHostname("some invalid name, which will still get accepted"));
    }
    
    @Test
    public void messageFragmentShowsUp() {
        String hostname = "";
        TextView tv = (TextView) sut.findViewById(R.id.editHostname);
        tv.setText(hostname);
        Button connect = (Button) sut.findViewById(R.id.button1);
        connect.performClick();
        View actual = sut.getCurrentFocus();
        View expected = new MessageFragment("").getView();
        assertEquals(expected, actual);
    }
}
