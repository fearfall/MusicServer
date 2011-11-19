package ru.musicserver.androidclient.model;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/28/11
 * Time: 10:25 AM
 */
public class Artist extends Model {
    //private String name;
    //private String mbid;
    private List<Album> albums = new LinkedList<Album>();

    public Artist(String name, String mbid) {
        this.name = name;
        this.mbid = mbid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (mbid != null ? !mbid.equals(artist.mbid) : artist.mbid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mbid != null ? mbid.hashCode() : 0;
    }

    @Override
    public Album[] getContent() {
        return albums.toArray(new Album[albums.size()]);
    }
}
