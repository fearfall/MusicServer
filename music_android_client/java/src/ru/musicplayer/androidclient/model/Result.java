package ru.musicplayer.androidclient.model;

import ru.musicplayer.androidclient.activity.MusicApplication;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/28/11
 * Time: 11:18 AM
 */
public class Result {
    private List<Artist> artists;
    private List<Album> albums;
    private List<Track> tracks;

    public Result () {
        artists = new LinkedList<Artist>();
        albums = new LinkedList<Album>();
        tracks = new LinkedList<Track>();
    }

    public Result(List<Artist> artists, List<Album> albums, List<Track> tracks) {
        this.artists = artists;
        this.albums = albums;
        this.tracks = tracks;
    }

    public boolean isEmpty() {
        return (artists.isEmpty() && albums.isEmpty() && tracks.isEmpty());
    }

    /*public List<Artist> getArtists() {
        return artists;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<Track> getTracks() {
        return tracks;
    }   */
    
    public Model[] getModelsOfType (int type) {
        switch (type) {
            case MusicApplication.ARTISTS:
                return getArtists();
            case MusicApplication.ALBUMS:
                return getAlbums();
            case MusicApplication.TRACKS:
                return getTracks();
            default:
                return new Model[]{new EmptyResult("No models of type " + type + ".")};
        }
    }

    public Artist[] getArtists() {
        return artists.toArray(new Artist[artists.size()]);
    }

    public Album[] getAlbums() {
        return albums.toArray(new Album[albums.size()]);
    }

    public Track[] getTracks() {
        return tracks.toArray(new Track[tracks.size()]);
    }


}
