package ru.musicserver.androidclient.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import ru.musicserver.androidclient.model.Model;
import ru.musicserver.androidclient.model.Playlist;

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