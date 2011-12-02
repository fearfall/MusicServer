package ru.musicserver.androidclient.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import ru.musicserver.androidclient.model.*;
import ru.musicserver.androidclient.network.Request;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/2/11
 * Time: 3:22 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AdapterHelper {
    private static final String singleTab = "      ";

    public static void addToAdapter (ArrayAdapter<Model> adapter, Model[] models, String parentShift) {
        for (Model model: models) {
            model.setShift(parentShift + singleTab);
            adapter.add(model);
        }
    }

    public static boolean isOpened (ArrayAdapter<Model> adapter, int position) {
        Model item = adapter.getItem(position);
        boolean opened = false;
        if (position != adapter.getCount()-1) {
            Model nextItem = adapter.getItem(position+1);
            if (nextItem.getShift().length() > item.getShift().length())
                opened = true;
        }
        return opened;
    }

    public static void close (ArrayAdapter<Model> adapter, int position) {
        boolean hasChild = true;
        int childPosition = position + 1;
        while (hasChild) {
            adapter.remove(adapter.getItem(childPosition));
            if (!AdapterHelper.isOpened(adapter, position))
                hasChild = false;
        }
    }

    public static void open (ArrayAdapter<Model> adapter, int position) throws IOException {
        Model item = adapter.getItem(position);
        String newShift = item.getShift() + singleTab;
        int childPosition = position + 1;

        Model trueItem = item;
        if (!(item instanceof ModelContainer)) {
            String type = (item instanceof Artist) ? "artist" : "album";
            trueItem = Request.get(type, item.getMbid());
        }
        for (Model child: trueItem.getContent()) {
            child.setShift(newShift);
            adapter.insert(child, childPosition);
            ++childPosition;
        }
    }
}
