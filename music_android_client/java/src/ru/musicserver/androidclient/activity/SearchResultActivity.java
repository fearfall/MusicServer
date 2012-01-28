package ru.musicserver.androidclient.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import ru.musicserver.androidclient.model.EmptyResult;
import ru.musicserver.androidclient.model.Model;
import ru.musicserver.androidclient.model.ModelContainer;
import ru.musicserver.androidclient.model.Result;
import ru.musicserver.androidclient.network.Request;

import java.io.IOException;

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
