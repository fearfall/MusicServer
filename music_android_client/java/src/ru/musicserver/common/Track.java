package ru.musicserver.common;

import ru.musicserver.android.ui.Item;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Track implements Item {
    private String myName;
    private String myUrl;

    public Track (String name, String url) {
        myName = name;
        myUrl = url;
    }

    public String getUrl() {
        return myUrl;
    }

    public Item.ItemType getType() {
        return ItemType.Track;
    }

    @Override
    public String toString() {
        return myName;
    }
}
