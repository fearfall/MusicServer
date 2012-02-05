package ru.musicplayer.androidclient.activity;

import android.os.Bundle;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 1/22/12
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultActivity extends OpenableListActivity {
    private int offset;
    private final int offsetStep = 10;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//, R.layout.search_result);
        setContentView(R.layout.search_result);
       // myListView = getListView();
       // setOnItemClickListener();
    }


}
