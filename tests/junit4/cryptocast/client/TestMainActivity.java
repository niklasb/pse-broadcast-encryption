package cryptocast.client;

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
}
