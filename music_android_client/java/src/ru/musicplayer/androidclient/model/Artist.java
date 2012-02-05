package ru.musicplayer.androidclient.model;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/28/11
 * Time: 10:25 AM
 */
public class Artist extends Model {
    private List<Album> albums = new LinkedList<Album>();

    public Artist(String name, String mbid) {
        super(name, mbid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (myMbid != null ? !myMbid.equals(artist.myMbid) : artist.myMbid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return myMbid != null ? myMbid.hashCode() : 0;
    }

    @Override
    public Album[] getContent() {
        return albums.toArray(new Album[albums.size()]);
    }
    
    public List<Album> getAlbums () {
        return albums;
    }

    @Override
    public void fillWith(Model model) {
        super.fillWith(model);
        albums = ((Artist)model).getAlbums();
    }
}
