package ru.musicplayer.androidclient.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import ru.musicplayer.androidclient.model.Playlist;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistActivity extends Activity {
    private ListView myListView;
    private MusicApplication myApplication;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists);
        myApplication = (MusicApplication) getApplication();
        myListView = (ListView)findViewById(R.id.playlist_list);
        myListView.setAdapter(new PlaylistArrayAdapter(PlaylistActivity.this, myApplication.getAllPlayListsArray()));

        final Dialog dialog = NewPlaylistDialog.create(myApplication, PlaylistActivity.this,
                (InputMethodManager)getSystemService(SearchActivity.INPUT_METHOD_SERVICE));
        
        Button newPlaylistButton = (Button)findViewById(R.id.newPlaylistButton);
        newPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }
    
    public void onResume() {
        //todo : check, what has changed and update
        super.onResume();
        myListView.setAdapter(new PlaylistArrayAdapter(PlaylistActivity.this, myApplication.getAllPlayListsArray()));
        //todo: remember dialog state?
    }

    public void newPlaylist(Playlist playlist) {
        //todo: highlight ?
        ((OpenableArrayAdapter) myListView.getAdapter()).append(playlist);
    }
}