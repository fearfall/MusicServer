package ru.musicserver.androidclient.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ru.musicserver.androidclient.model.*;
import ru.musicserver.androidclient.network.Request;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistActivity extends OpenableListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists);
        myListView = getListView();
        setOnItemClickListener();
    }

    public void onResume() {
        //todo : check, what has changed and update
        super.onResume();

        LinkedList<Playlist> playLists = MainActivity.getAllPlayLists();
        ArrayAdapter<Model> adapter = new ArrayAdapter<Model>(PlaylistActivity.this, R.layout.unplayable);

        for (Playlist playlist: playLists) {
            adapter.add(playlist.toModelContainer());
            AdapterHelper.addToAdapter(adapter, playlist.getData(), "");
        }
        setAdapter(adapter);
    }
}