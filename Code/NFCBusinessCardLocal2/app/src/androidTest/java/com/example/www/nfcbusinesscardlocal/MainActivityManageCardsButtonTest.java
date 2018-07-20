package com.example.www.nfcbusinesscardlocal;

import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

public class MainActivityManageCardsButtonTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    public MainActivity mActivity = null;


    @Before
    public void setUp() throws Exception {
        mActivity = mainActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunchOfManageCardButtonClick(){
        assertNotNull(mActivity.findViewById(R.id.openManageCards));
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(ManageCards.class.getName(),null,false);
        final Button button = (Button) mActivity.findViewById(R.id.openManageCards);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click button and open next activity.
                button.performClick();
            }
        });
        ManageCards nextActivity = (ManageCards) getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(nextActivity);
        nextActivity .finish();


    }


    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}