package ru.musicplayer.androidclient.activity;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ru.musicplayer.androidclient.model.*;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/2/11
 * Time: 3:57 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class OpenableListActivity extends Activity {
   // protected ListView myListView;

   /* public void onCreate(Bundle savedInstanceState, int layoutResId) {
        super.onCreate(savedInstanceState);
        /*setContentView(layoutResId);
        myListView = getListView();
        setOnItemClickListener();
    }                                       */

    protected void showErrorMessage (String title, String message) {
        ((MusicApplication)getApplication()).showErrorMessage(title, message);
    }

    public void setAdapter (ListView listView, ArrayAdapter<Model> adapter) {
        if (adapter == null)
            return;
        listView.setAdapter(adapter);
    }

    protected void setOnListItemClickListener (final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getId() == R.id.add_to_playlist) {
                    Model item = (Model) adapterView.getItemAtPosition(position);
                    if (item instanceof Track)
                        ((MusicApplication)getApplicationContext()).getCurrentPlaylist().add((Track) item);
                    else if (item instanceof Album)
                        ((MusicApplication)getApplicationContext()).getCurrentPlaylist().addTracks(((Album) item).getTracks());
                    else if (item instanceof Artist) {
                        for (Album album: ((Artist) item).getAlbums()) {
                            ((MusicApplication)getApplicationContext()).getCurrentPlaylist().addTracks(album.getTracks());
                        }
                    }
                    return;
                }
                
                Model item = (Model)adapterView.getItemAtPosition(position);
                if (item instanceof Track) {
                    ((MusicApplication)getApplication()).trackClicked((Track)item);
                    return;
                }
                if (item instanceof EmptyResult)
                    return;

                OpenableArrayAdapter adapter = (OpenableArrayAdapter) adapterView.getAdapter();
                if (adapter.isOpened(position)) {
                    adapter.close(position);
                } else {
                    try {
                        adapter.open(position);
                    } catch (IOException e) {
                        showErrorMessage("Open list item", e.getMessage());
                        return;
                    }
                }
                setAdapter(listView, adapter);
            }
        });
    }
}
