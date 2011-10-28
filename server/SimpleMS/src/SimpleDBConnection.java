import model.Album;
import model.Artist;
import model.Result;
import model.Track;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/18/11
 * Time: 1:34 PM
 */
public class SimpleDBConnection {
    private JdbcTemplate jdbcTemplate;

    public SimpleDBConnection() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");//:1521
        dataSource.setUrl("jdbc:postgresql://localhost/musicbrainz_db");
        dataSource.setUsername("musicbrainz");
        dataSource.setPassword("");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Result search(String pattern) {
        return new Result(
            getArtists(pattern),
            getAlbums(pattern),
            getTracks(pattern)
                );
    }

    private List<Artist> getArtists(String pattern) {
        final List<Artist> artists = new ArrayList<Artist>();
        String queryArtist = ("select * from simple_artist_info where lower(name) like \'%\' || ? || \'%\'");
        jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                Artist artist = new Artist(resultSet.getString("name"), resultSet.getString("mbid"));
                if(artists.indexOf(artist) < 0) {
                    artists.add(artist);
                }
            }
        }, pattern.toLowerCase());
        return artists;
    }

    private List<Album> getAlbums(String pattern) {
        final List<Album> albums = new ArrayList<Album>();
        String queryAlbum = ("select * from simple_album_info where lower(release) like \'%\' || ? || \'%\'");
        jdbcTemplate.query(queryAlbum, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                Album album = new Album(resultSet.getString("release"),resultSet.getString("mbid"));
                if(albums.indexOf(album) < 0) {
                    albums.add(album);
                }
            }
        }, pattern.toLowerCase());
        return albums;
    }

    private List<Track> getTracks(String pattern) {
        final List<Track> tracks = new ArrayList<Track>();
        String queryTrack = ("select * from simple_track_info where lower(track) like \'%\' || ? || \'%\'");
        jdbcTemplate.query(queryTrack, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                Track track = new Track(resultSet.getString("track"),
                                        resultSet.getString("url"),
                                        resultSet.getString("mbid"));
                if(tracks.indexOf(track) < 0) {
                    tracks.add(track);
                }
            }
        }, pattern.toLowerCase());
        return tracks;
    }
}