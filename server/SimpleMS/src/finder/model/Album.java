package finder.model;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/28/11
 * Time: 10:26 AM
 */
public class Album {
    private String name = "";
    private String mbid = "";
    private List<Track> tracks = new LinkedList<Track>();

    public Album(String name, String mbid) {
        this.name = name;
        this.mbid = mbid;

    }

    public Album() {}

    public boolean addTrack(Track track) {
        if(tracks.indexOf(track) < 0) {
            tracks.add(track);
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
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

        Album album = (Album) o;

        if (mbid != null ? !mbid.equals(album.mbid) : album.mbid != null) return false;

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
        return (isPartValid() && tracks != null && !tracks.isEmpty());
    }
}
