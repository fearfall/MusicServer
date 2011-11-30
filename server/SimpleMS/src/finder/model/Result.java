package finder.model;

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
        this.artists = artists;
        this.albums = albums;
        this.tracks = tracks;
    }

    public boolean isValid() {
        return !(artists.isEmpty() && albums.isEmpty() && tracks.isEmpty());
    }
}
