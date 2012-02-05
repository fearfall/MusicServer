package ru.musicplayer.androidclient.model;

import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 05/02/12
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistResult {
    private List<PlaylistResultEntry> myPlaylist = null;
    private String myStatus = null;
    
    public PlaylistResult (String status) {
        myStatus = status;
    }

    public PlaylistResult (List<PlaylistResultEntry> playlist) {
        myPlaylist = playlist;
    }

    public List<PlaylistResultEntry> getPlaylist() {
        return myPlaylist;
    }
    
    public Playlist toPlaylist (String name) throws IOException {
        Playlist playlist = new Playlist(name);
        Collections.sort(myPlaylist);
        for (PlaylistResultEntry entry: myPlaylist) {
            Track track = (Track) Request.get("track", entry.getMbid());
            playlist.loadTrack(track);
        }
        return playlist;
    }

    public String getStatus() {
        return myStatus;
    }
}
