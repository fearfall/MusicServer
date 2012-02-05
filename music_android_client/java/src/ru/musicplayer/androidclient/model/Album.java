package ru.musicplayer.androidclient.model;

import java.util.LinkedList;
import java.util.List;

public class Album extends Model {
    private List<Track> tracks = new LinkedList<Track>();

    public Album(String name, String mbid) {
        super(name, mbid);
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

        if (myMbid != null ? !myMbid.equals(album.myMbid) : album.myMbid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return myMbid != null ? myMbid.hashCode() : 0;
    }

    @Override
    public Track[] getContent() {
        return tracks.toArray(new Track[tracks.size()]);
    }

    @Override
    public void fillWith(Model model) {
        super.fillWith(model);
        tracks = ((Album) model).getTracks();
    }
}
