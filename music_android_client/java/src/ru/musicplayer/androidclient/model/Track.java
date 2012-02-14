package ru.musicplayer.androidclient.model;


/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/18/11
 * Time: 4:04 PM
 */
public class Track extends Model {
    private String url;

    public Track(String name, String mbid, String url) {
        //super(name, mbid);
        this.name = name;
        this.mbid = mbid;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public Track[] getContent() {
        return null;
    }

    @Override
    public void fillWith(Model model) {
        super.fillWith(model);
        url = ((Track) model).getUrl();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (mbid != null ? !mbid.equals(track.mbid) : track.mbid != null) return false;
        if (name != null ? !name.equals(track.name) : track.name != null) return false;
        if (url != null ? !url.equals(track.url) : track.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
    
    public Track (PlaylistResultEntry entry) {
        mbid = entry.getMbid();
        name = entry.getTitle();
    }
}
