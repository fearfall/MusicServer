package ru.musicserver.androidclient.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/2/11
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayingActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textview = new TextView(this);
                textview.setText("This is the Playing tab");
                setContentView(textview);
    }
}