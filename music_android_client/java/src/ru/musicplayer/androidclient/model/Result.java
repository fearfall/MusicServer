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
    private SmallResult<Artist> artists = new SmallResult<Artist>();
    private SmallResult<Album> albums = new SmallResult<Album>();
    private SmallResult<Track> tracks = new SmallResult<Track>();

    public Result(SmallResult<Artist> artists, SmallResult<Album> albums, SmallResult<Track> tracks) {
        this.artists = artists;
        this.albums = albums;
        this.tracks = tracks;
    }

    public Result () {
    }

    public boolean isEmpty() {
        return (artists.isEmpty() && albums.isEmpty() && tracks.isEmpty());
    }

    public Model[] getModelsOfType (int type) {
        switch (type) {
            case MusicApplication.ARTISTS:
                return artists.getModels().toArray(new Model[artists.getModels().size()]);
            case MusicApplication.ALBUMS:
                return albums.getModels().toArray(new Model[albums.getModels().size()]);
            case MusicApplication.TRACKS:
                return tracks.getModels().toArray(new Model[tracks.getModels().size()]);
            default:
                return new Model[]{new EmptyResult("No models of type " + type + ".")};
        }
    }

   /* public Artist[] getArtists() {
        List<Artist> m = (List<Artist>) artists.getModels();
        return m.toArray(new Artist[m.size()]);
    }

    public Album[] getAlbums() {
        List<Album> m = (List<Album>) albums.getModels();
        return m.toArray(new Album[m.size()]);
    }

    public Track[] getTracks() {
        List<Track> m = (List<Track>) tracks.getModels();
        return m.toArray(new Track[m.size()]);
    }                  */
    
    public int getArtistsOffset() {
        return artists.getOffset();
    }

    public int getAlbumsOffset() {
        return albums.getOffset();
    }

    public int getTracksOffset() {
        return tracks.getOffset();
    }
}
