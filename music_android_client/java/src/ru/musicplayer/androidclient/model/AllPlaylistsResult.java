package ru.musicplayer.androidclient.model;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 05/02/12
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class AllPlaylistsResult {
    private String myStatus = null;
    private List<String> myPlaylists = null;

    public AllPlaylistsResult (String status) {
        myStatus = status;
    }

    public AllPlaylistsResult (List<String> playlists) {
        myPlaylists = playlists;
    }

    public AllPlaylistsResult (PlaylistList playlists) {
        myPlaylists = playlists.getData();
    }

    public String getMyStatus() {
        return myStatus;
    }

    public List<String> getMyPlaylists() {
        return myPlaylists;
    }
}
