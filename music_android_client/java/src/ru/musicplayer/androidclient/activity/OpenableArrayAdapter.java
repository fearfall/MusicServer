package ru.musicplayer.androidclient.activity;

import ru.musicplayer.androidclient.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import ru.musicplayer.androidclient.model.*;
import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 24/01/12
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class OpenableArrayAdapter extends ArrayAdapter<Model> {
    private final String singleTab = "      ";
    private final Context myContext;
    private final MusicApplication myApplication;

    public OpenableArrayAdapter(Context context, Model... objects) {
        super(context, R.layout.search_item, R.id.name);
        myContext = context;
        myApplication = ((MusicApplication)context.getApplicationContext());
        setContent("", objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.name);
        textView.setText(getItem(position).toString());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model item = getItem(position);
                if (item instanceof Track) {
                    myApplication.trackClicked((Track)item);
                    return;
                }
                if (item instanceof EmptyResult)
                    return;
                if (isOpened(position)) {
                    close(position);
                } else {
                    try {
                        open(position);
                    } catch (IOException e) {
                        myApplication.showErrorMessage("Open list item", e.getMessage());
                    }
                }
            }
        });
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
        return rowView;
    }

    private void setContent (String parentShift, Model[] values) {
        if (values == null || values.length == 0) {
            return;
        }
        for (Model model: values) {
            model.setShift(parentShift + singleTab);
            add(model);
        }
    }
    
    public void append (Model[] objects) {
        for (Model model: objects) {
            model.setShift("" + singleTab);
            add(model);
        }
    }

    public boolean isOpened (int position) {
        Model item = getItem(position);
        boolean opened = false;
        if (position != getCount()-1) {
            Model nextItem = getItem(position+1);
            if (nextItem.getShift().length() > item.getShift().length())
                opened = true;
        }
        return opened;
    }

    public void close (int position) {
        boolean hasChild = true;
        int childPosition = position + 1;
        while (hasChild) {
            remove(getItem(childPosition));
            if (!isOpened(position))
                hasChild = false;
        }
    }

    public void open (int position) throws IOException {
        Model item = getItem(position);
        String newShift = item.getShift() + singleTab;
        int childPosition = position + 1;

        Model trueItem = item;
        if (item.needsUpdate()) {
            String type = item.getClass().getSimpleName().toLowerCase();
            try {
                if (item instanceof Playlist) {
                    PlaylistResult result = Request.getPlaylist(item.getName());
                    if (result.getStatus() != null) {
                        myApplication.showToast(result.getStatus());
                        return;
                    }
                    trueItem = result.toPlaylist(item.getName());
                }
                else {
                    trueItem = Request.get(type, item.getMbid());
                    if (trueItem == null) {
                        myApplication.showToast(item.getName() + " is empty");
                        return;
                    }
                }
                item.fillWith(trueItem);
            } catch (Exception e) {
                myApplication.showErrorMessage("Open " + item.getName(), e.getMessage());
                return;
            }
        }

        for (Model child: item.getContent()) {
            child.setShift(newShift);
            insert(child, childPosition);
            ++childPosition;
        }
    }
}
