package ru.musicserver.common;

import ru.musicserver.android.ui.Item;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 1:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Artist implements Item {
    private String myName;
    private String myUrl;
    private List<Album> myAlbums;

    public Artist (String name, String url) {
        myName = name;
        myUrl = url;
    }

    public String getUrl() {
        return myUrl;
    }

    public ItemType getType() {
        return ItemType.Artist;
    }

    @Override
    public String toString() {
        return myName;
    }
}
