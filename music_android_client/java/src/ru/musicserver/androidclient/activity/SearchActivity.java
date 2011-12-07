package ru.musicserver.androidclient.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ru.musicserver.androidclient.model.*;
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
    private Result myResult = null;
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
                offset = 0;
                searchBack.setEnabled(false);
                searchFwd.setEnabled(true);
                ArrayAdapter<Model> adapter = onSearchClick(view);
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
                    Toast.makeText(getApplicationContext(), "You have reached the end of search result", Toast.LENGTH_LONG).show();
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
                if (offset < offsetStep)
                    offset = offsetStep;
                ArrayAdapter<Model> adapter = onSearchClick(view);
                if (adapter == null)
                    return;
                setAdapter(adapter);
            }
        });

        setOnItemClickListener();
    }

    private ArrayAdapter<Model> onSearchClick(View v) {
        EditText searchEdit = (EditText) findViewById(R.id.searchEditText);
        String searchString = searchEdit.getText().toString();

        try {
            myResult = Request.search(searchString, offsetStep, offset);
        } catch (IOException e) {
            showErrorMessage(e.getMessage());
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
