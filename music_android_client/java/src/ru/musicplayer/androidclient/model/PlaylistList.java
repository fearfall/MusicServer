package ru.musicplayer.androidclient.model;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/02/12
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistList {
    private List<String> data;
    
    public PlaylistList (List<String> d) {
        data = d;
    }

    public List<String> getData() {
        return data;
    }
}
