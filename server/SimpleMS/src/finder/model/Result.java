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
    private SmallResult<Artist> artists;
    private SmallResult<Album> albums;
    private SmallResult<Track> tracks;

    public Result(SmallResult<Artist> artists, SmallResult<Album> albums, SmallResult<Track> tracks) {
        this.artists = artists == null ? new SmallResult<Artist>() : artists;
        this.albums = albums == null ? new SmallResult<Album>() : albums;
        this.tracks = tracks == null ? new SmallResult<Track>() : tracks;
    }

    public boolean isValid() {
        return !(artists.isEmpty() && albums.isEmpty() && tracks.isEmpty());
    }
}
