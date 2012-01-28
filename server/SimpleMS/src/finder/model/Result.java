package finder.model;

import java.util.ArrayList;
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

    public Result(List<Artist> artists, List<Album> albums, List<Track> tracks) {
        this.artists = artists == null ? new ArrayList<Artist>() : artists;
        this.albums = albums == null ? new ArrayList<Album>() : albums;
        this.tracks = tracks == null ? new ArrayList<Track>() : tracks;
    }

    public boolean isValid() {
        return !(artists.isEmpty() && albums.isEmpty() && tracks.isEmpty());
    }
}
