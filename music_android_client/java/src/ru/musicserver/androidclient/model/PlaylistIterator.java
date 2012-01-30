package ru.musicserver.androidclient.model;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 30/01/12
 * Time: 21:38
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistIterator {
    public static Track next(Playlist playlist) {
        if (!playlist.isPlaying() || playlist.isAtTheEnd())
            return null;
        playlist.inc();
        return playlist.getPlayingTrack();
    }

    public static Track back(Playlist playlist) {
        if (!playlist.isPlaying() || playlist.isAtTheBeginning())
            return null;
        playlist.dec();
        return playlist.getPlayingTrack();
    }
}
