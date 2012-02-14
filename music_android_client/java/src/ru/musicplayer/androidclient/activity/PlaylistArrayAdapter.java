package ru.musicplayer.androidclient.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;
import ru.musicplayer.androidclient.model.*;
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

    //private AlertDialog.Builder builder;

    public PlaylistArrayAdapter(Context context, Model... objects) {
        super(context, R.layout.playlists_item, R.id.name, false,objects);

    }

    @Override
    protected void setViewControls(View rowView, final int position) {
        ImageButton playButton = (ImageButton) rowView.findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myApplication.setCurrentPlaylist(position);
            }
        });

        ImageButton editButton = (ImageButton) rowView.findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: set in PlayingActivity
                myApplication.showToast("edit");
            }
        });

        ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Model item = getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
                builder.setMessage("Are you sure you want to remove playlist "+ item.getName() +"?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    String result = Request.playlistAction("remove", item.getName());
                                    myApplication.showToast(result);
                                    if (result.contains("success")) {
                                        remove(item);
                                        myApplication.removePlaylist(position);
                                    }
                                } catch (IOException e) {
                                    myApplication.showErrorMessage("Remove playlist", e.getMessage());
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    protected Model get(Model item) throws IOException {
        if (item.getName().equals("Search history")) {
            return myApplication.getHistory();
        }
        PlaylistResult result = Request.getPlaylist(item.getName());
        if (result.getStatus() != null) {
            myApplication.showToast(result.getStatus());
            return null;
        }
        return result.toPlaylist(item.getName());
    }
}
