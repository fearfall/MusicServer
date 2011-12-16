package ru.musicserver.androidclient.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.musicserver.androidclient.model.EmptyResult;
import ru.musicserver.androidclient.model.Model;
import ru.musicserver.androidclient.model.ModelContainer;
import ru.musicserver.androidclient.model.Result;
import ru.musicserver.androidclient.network.Request;

import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends OpenableListActivity {
    private int offset;
    private ImageButton searchFwd;
    private ImageButton searchBack;
    private final int offsetStep = 10;

     /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        myListView = getListView();
        searchFwd = (ImageButton) findViewById(R.id.searchForwardButton);
        searchBack = (ImageButton) findViewById(R.id.searchBackwardButton);
        searchBack.setEnabled(false);
        searchFwd.setEnabled(false);

        final Button searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(SearchActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 1);
                offset = 0;
                ArrayAdapter<Model> adapter = onSearchClick(view);
                if (adapter == null)
                    return;
                searchBack.setEnabled(false);
                if (adapter.getCount() > 1)
                    searchFwd.setEnabled(true);
                setAdapter(adapter);
            }
        });

        searchFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset += offsetStep;
                ArrayAdapter<Model> adapter = onSearchClick(view);
                if (adapter == null)
                    return;
                if (adapter.getCount() == 1) {
                    MainActivity.showToast("You have reached the end of search result", getApplicationContext());
                    searchFwd.setEnabled(false);
                    return;
                }
                setAdapter(adapter);
                searchBack.setEnabled(true);
            }
        });

        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset -= offsetStep;
                if (offset < 0) {
                    searchBack.setEnabled(false);
                    offset = 0;
                    return;
                }
                ArrayAdapter<Model> adapter = onSearchClick(view);
                if (adapter == null)
                    return;
                setAdapter(adapter);
            }
        });
        setOnItemClickListener();

        final EditText searchEdit = (EditText) findViewById(R.id.searchEditText);
        searchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (view.getId() != R.id.searchEditText)
                    return false;
                if (keyCode != 66) // = Enter
                    return false;
                if (keyEvent.getAction() != 0)
                    return false;
                InputMethodManager imm = (InputMethodManager)getSystemService(SearchActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 1);
                searchButton.performClick();
                return true;
            }
        });
    }

    private ArrayAdapter<Model> onSearchClick(View v) {
        EditText searchEdit = (EditText) findViewById(R.id.searchEditText);
        String searchString = searchEdit.getText().toString();
        Result myResult;
        try {
            myResult = Request.search(searchString, offsetStep, offset);
        } catch (IOException e) {
            showErrorMessage("Search", e.getMessage());
            return null;
        }
        ArrayAdapter<Model> adapter = new ArrayAdapter<Model>(v.getContext(), R.layout.unplayable);
        if (myResult.isEmpty()) {
            adapter.add(new EmptyResult("No items were found."));
        } else {
            adapter.add(new ModelContainer("Artists", myResult.getArtists()));
            AdapterHelper.addToAdapter(adapter, myResult.getArtists(), "");
            adapter.add(new ModelContainer("Albums", myResult.getAlbums()));
            AdapterHelper.addToAdapter(adapter, myResult.getAlbums(), "");
            adapter.add(new ModelContainer("Tracks", myResult.getTracks()));
            AdapterHelper.addToAdapter(adapter, myResult.getTracks(), "");
        }
        return adapter;
    }


}
