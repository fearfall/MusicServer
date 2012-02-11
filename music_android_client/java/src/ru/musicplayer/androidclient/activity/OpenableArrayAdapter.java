package ru.musicplayer.androidclient.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.musicplayer.androidclient.model.EmptyResult;
import ru.musicplayer.androidclient.model.Model;
import ru.musicplayer.androidclient.model.Track;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 24/01/12
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class OpenableArrayAdapter extends ArrayAdapter<Model> {
    protected final String singleTab = "      ";
    protected final Context myContext;
    protected final MusicApplication myApplication;
    protected final int myResource;
    protected final int myTextViewResource;
    protected boolean writeToHistory;
        
    public OpenableArrayAdapter(Context context, int resource, int textViewResource, boolean doWriteToHistory, Model... objects) {
        super(context, resource, textViewResource);
        myContext = context;
        myApplication = ((MusicApplication)context.getApplicationContext());
        myResource = resource;
        myTextViewResource = textViewResource;
        writeToHistory = doWriteToHistory;
        setContent("", objects);
    }
    
    protected abstract void setViewControls (View rowView, final int position);

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(myResource, parent, false);
        TextView textView = (TextView) rowView.findViewById(myTextViewResource);
        textView.setText(getItem(position).toString());
        
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model item = getItem(position);
                if (item instanceof Track) {
                    myApplication.trackClicked((Track)item, writeToHistory);
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
        
        setViewControls(rowView, position);
        
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
    
    public void append (Model... objects) {
        if (objects == null)
            return;
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
    
    protected abstract Model get(Model item) throws IOException;

    public void open (int position) throws IOException {
        Model item = getItem(position);
        String newShift = item.getShift() + singleTab;
        int childPosition = position + 1;

        if (item.needsUpdate()) {
            try {
                Model trueItem = get(item);
                if (trueItem == null)
                    return;
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
