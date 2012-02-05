/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 05/02/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
package ru.musicplayer.androidclient.test;

import android.test.ActivityInstrumentationTestCase2;
import ru.musicplayer.androidclient.activity.MainActivity;
import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;

public class RequestAndroidTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mActivity;  // the activity under test
    //private String resourceString;

    public RequestAndroidTest() {
        super("ru.musicserver.androidclient.activity", MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
       // mView = (TextView) mActivity.findViewById(com.example.helloandroid.R.id.textview);
       // resourceString = mActivity.getString(com.example.helloandroid.R.string.hello);
    }

    public void testPreconditions() {
        //assertNotNull(mView);
    }

    public void testText() throws IOException {
        String r = Request.playlistAction("create", "one");
        assertTrue(r.contains("success"));
    }
}