package ru.musicserver.common;

import ru.musicserver.android.ui.Item;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Album implements Item {
    private String myName;
    private String myUrl;
    private List<Track> myTracks;

    public Album (String name, String url) {
        myName = name;
        myUrl = url;
    }

    public String getUrl() {
        return myUrl;
    }

    public ItemType getType() {
        return ItemType.Album;
    }

    @Override
    public String toString() {
        return myName;
    }
}
