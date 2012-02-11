package ru.musicplayer.androidclient.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import ru.musicplayer.androidclient.model.Album;
import ru.musicplayer.androidclient.model.Artist;
import ru.musicplayer.androidclient.model.Model;
import ru.musicplayer.androidclient.model.Track;
import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 07/02/12
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class SearchArrayAdapter extends OpenableArrayAdapter {

    public SearchArrayAdapter(Context context, Model... objects) {
        super(context, R.layout.search_item, R.id.name, true, objects);
    }

    @Override
    protected void setViewControls(View rowView, final int position) {
        ImageButton addButton = (ImageButton) rowView.findViewById(R.id.add_to_playlist);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model item = getItem(position);
                if (item instanceof Track)
                    myApplication.getCurrentPlaylist().add((Track) item);
                else if (item instanceof Album)
                    myApplication.getCurrentPlaylist().addTracks(((Album) item).getTracks());
                else if (item instanceof Artist) {
                    for (Album album: ((Artist) item).getAlbums()) {
                        myApplication.getCurrentPlaylist().addTracks(album.getTracks());
                    }
                }
            }
        });
    }

    @Override
    protected Model get(Model item) throws IOException {
        String type = item.getClass().getSimpleName().toLowerCase();
        Model trueItem = Request.get(type, item.getMbid());
        if (trueItem == null) {
            myApplication.showToast(item.getName() + " is empty");
        }
        return trueItem;
    }
}
