package ru.musicserver.androidclient.model;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/28/11
 * Time: 10:26 AM
 */
public class Album extends Model {
    //private String name;
    //private String mbid;
    private List<Track> tracks = new LinkedList<Track>();

    public Album(String name, String mbid) {
        this.name = name;
        this.mbid = mbid;
    }

    public boolean addTrack(Track track) {
        if(tracks.indexOf(track) < 0) {
            tracks.add(track);
            return true;
        }
        return false;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        if (mbid != null ? !mbid.equals(album.mbid) : album.mbid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mbid != null ? mbid.hashCode() : 0;
    }

    @Override
    public Track[] getContent() {
        return tracks.toArray(new Track[tracks.size()]);
    }

}
