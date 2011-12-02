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

     /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        myListView = getListView();

        final Button button = (Button)findViewById(R.id.searchButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchEdit = (EditText)findViewById(R.id.searchEditText);
                String searchString = searchEdit.getText().toString();

                try {
                    myResult = Request.search(searchString);
                } catch (IOException e) {
                    showErrorMessage(e.getMessage());
                    return;
                }

                ArrayAdapter<Model> adapter = new ArrayAdapter<Model>(v.getContext(), R.layout.unplayable);


                if (myResult.isEmpty()) {
                    adapter.add(new EmptyResult("No items were found."));
                } else {
                    adapter.add(new ModelContainer("Artist", myResult.getArtists()));
                    AdapterHelper.addToAdapter(adapter, myResult.getArtists(), "");
                    adapter.add(new ModelContainer("Album", myResult.getAlbums()));
                    AdapterHelper.addToAdapter(adapter, myResult.getAlbums(), "");
                    adapter.add(new ModelContainer("Track", myResult.getTracks()));
                    AdapterHelper.addToAdapter(adapter, myResult.getTracks(), "");
                }
                setAdapter(adapter);
            }
        });

        setOnItemClickListener();
    }

}
