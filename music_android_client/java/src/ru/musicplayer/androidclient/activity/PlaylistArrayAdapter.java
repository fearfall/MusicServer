package ru.musicplayer.androidclient.activity;

import android.content.Context;
import android.view.View;
import ru.musicplayer.androidclient.model.Model;
import ru.musicplayer.androidclient.model.PlaylistResult;
import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 07/02/12
 * Time: 23:10
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistArrayAdapter extends OpenableArrayAdapter {

    public PlaylistArrayAdapter(Context context, Model... objects) {
        super(context, R.layout.playlists_item, R.id.name, objects);
    }
    @Override
    protected void setViewControls(View rowView, int position) {
        //todo
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Model get(Model item) throws IOException {
        PlaylistResult result = Request.getPlaylist(item.getName());
        if (result.getStatus() != null) {
            myApplication.showToast(result.getStatus());
            return null;
        }
        return result.toPlaylist(item.getName());
    }
}
