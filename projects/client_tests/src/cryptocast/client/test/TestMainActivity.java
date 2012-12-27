package cryptocast.client.test;

import cryptocast.client.MainActivity;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityTestCase;

public class TestMainActivity extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mActivity;
    
    public TestMainActivity() {
        super("cryptocast.client", MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
      super.setUp();
      
      mActivity = getActivity();
    }
    
    public void testPreConditions() {
        assertTrue(mActivity != null);
    }

    public void testServerNameSaving() {
        String expected = "serverName";
        mActivity.storeServerName(expected);
        String actual = mActivity.loadServerName();
        assertEquals(expected, actual);
    }
}
