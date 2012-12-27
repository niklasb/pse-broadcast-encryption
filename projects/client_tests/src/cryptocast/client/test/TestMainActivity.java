package cryptocast.client.test;

import cryptocast.client.MainActivity;
import android.app.Activity;
import android.test.ActivityTestCase;

public class TestMainActivity extends ActivityTestCase {
    
    public void testServerNameSaving() {
        MainActivity mActivity = new MainActivity();
        String expected = "serverName";
        mActivity.storeServerName(expected);
        //String actual = mActivity.loadServerName();
        //assertEquals(expected, actual);
    }
}
