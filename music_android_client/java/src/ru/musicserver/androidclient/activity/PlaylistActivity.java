package ru.musicserver.androidclient.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textview = new TextView(this);
                textview.setText("This is the Playlists tab");
                setContentView(textview);


    }
}