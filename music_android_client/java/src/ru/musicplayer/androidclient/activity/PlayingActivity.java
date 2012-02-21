package ru.musicplayer.androidclient.activity;

import android.app.ListActivity;
import android.os.Bundle;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/2/11
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayingActivity extends ListActivity {
    private MusicApplication myApplication;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);
        myApplication = (MusicApplication) getApplication();
        myApplication.register(this);

        /*TextView textview = new TextView(this);
                textview.setText("This is the Playing tab");
                setContentView(textview); */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApplication.remove(this);
    }
}