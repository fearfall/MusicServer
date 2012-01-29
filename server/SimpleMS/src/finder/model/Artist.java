package finder.model;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/28/11
 * Time: 10:25 AM
 */
public class Artist {
    private String name = "";
    private String mbid = "";
    private List<Album> albums = new LinkedList<Album>();

    public Artist(String name, String mbid) {
        this.name = name;
        this.mbid = mbid;
    }

    public Artist() {}

    /*public boolean addAlbum(Album album) {
        int albIndex = 0;
        if((albIndex = albums.indexOf(album)) < 0) {
            albums.add(album);
            return true;
        }
        else {
            for (Track track : album.getTracks()) {
                albums.get(albIndex).addTrack(track);
            }
            return false;
        }
    }

    public void addTrack(Track track) {
        for (Album album : albums) {
            if(album.addTrack(track)) {
                break;
            }
        }
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
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

    public boolean isPartValid() {
        return !(name == null || name.isEmpty()
               || mbid.isEmpty() || mbid == null);
    }
    
    public boolean isAllValid() {
        return (isPartValid() && albums != null && !albums.isEmpty());
    }
}
