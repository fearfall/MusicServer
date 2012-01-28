package finder.handlers;

import finder.model.*;
import finder.model.ResultCount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/18/11
 * Time: 1:34 PM
 */
public class SimpleDBConnection {
    private JdbcTemplate jdbcTemplate;

    public SimpleDBConnection() throws SQLException {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");//:1521
            dataSource.setUrl("jdbc:postgresql://localhost/musicbrainz_db");
            dataSource.setUsername("musicbrainz");
            dataSource.setPassword("");
            this.jdbcTemplate = new JdbcTemplate(dataSource);
            dataSource.getConnection();
    }

    public Result search(String pattern, int offset, int limit) {
        Result result = new Result(
                findArtists(pattern, offset, limit),
                findAlbums(pattern, offset, limit),
                findTracks(pattern, offset, limit)
        );
        return result.isValid() ? result : null;
    }
    
    public Result searchArtists(String pattern, int offset, int limit) {
        Result result = new Result(findArtists(pattern, offset, limit), null, null);
        return result.isValid() ? result : null;
    }

    public Result searchAlbums(String pattern, int offset, int limit) {
        Result result = new Result(null, findAlbums(pattern, offset, limit), null);
        return result.isValid() ? result : null;
    }

    public Result searchTracks(String pattern, int offset, int limit) {
        Result result = new Result(null, null, findTracks(pattern, offset, limit));
        return result.isValid() ? result : null;
    }

    public ResultCount getTotalAmount(String pattern) {
        return new ResultCount(getArtistsCount(pattern), getAlbumsCount(pattern), getTracksCount(pattern));
    }

    private int getArtistsCount (String pattern) {
        final int[] count = {0};
        String queryArtist = ("select count(*) from simple_artist_info where lower(name) like '%'||?||'%' ");
        try {
            jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    count[0] = Integer.parseInt(resultSet.getString("count"));
                }
            }, pattern.toLowerCase());
        } catch (Exception e) {e.printStackTrace();}
        return count[0];
    }

    private int getAlbumsCount (String pattern) {
        final int[] count = {0};
        String queryArtist = ("select count(*) from simple_album_info where lower(name) like '%'||?||'%' ");
        try {
            jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    count[0] = Integer.parseInt(resultSet.getString("count"));
                }
            }, pattern.toLowerCase());
        } catch (Exception e) {e.printStackTrace();}
        return count[0];
    }

    private int getTracksCount (String pattern) {
        final int[] count = {0};
        String queryTrack = ("select count(*) from simple_track_info where lower(name) like '%'||?||'%' ");
        try {
            jdbcTemplate.query(queryTrack, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    count[0] = Integer.parseInt(resultSet.getString("count"));
                }
            }, pattern.toLowerCase());
        } catch (Exception e) {e.printStackTrace();}
        return count[0];
    }

    private List<Artist> findArtists(String pattern, int offset, int limit) {
        final List<Artist> artists = new ArrayList<Artist>();
        String queryArtist = ("select * from simple_artist_info where lower(name) like '%'||?||'%' order by mbid limit ? offset ? ");
        try {
        jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                Artist artist = new Artist(resultSet.getString("name"), resultSet.getString("mbid"));
                if(artists.indexOf(artist) < 0) {
                    if(artist.isValid()) {
                        artists.add(artist);
                    }
                }
            }
        }, pattern.toLowerCase(), limit, offset);
        } catch (Exception e) {e.printStackTrace();}
        return artists;
    }

    private List<Album> findAlbums(String pattern, int offset, int limit) {
        final List<Album> albums = new ArrayList<Album>();
        String queryAlbum = ("select * from simple_album_info where lower(name) like '%'||?||'%' order by mbid limit ? offset ?");
        try {
            jdbcTemplate.query(queryAlbum, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    Album album = new Album(resultSet.getString("name"),resultSet.getString("mbid"));
                    if(albums.indexOf(album) < 0) {
                        if(album.isValid()) {
                            albums.add(album);
                        }
                    }
                }
            }, pattern.toLowerCase(), limit, offset);
        } catch (Exception e) {e.printStackTrace();}
        return albums;
    }

    private List<Track> findTracks(String pattern, int offset, int limit) {
        final List<Track> tracks = new ArrayList<Track>();
        String queryTrack = ("select * from simple_track_info where lower(name) like '%'||?||'%' order by mbid limit ? offset ?");
        try {
            jdbcTemplate.query(queryTrack, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    Track track = new Track(resultSet.getString("name"),
                                            resultSet.getString("url"),
                                            resultSet.getString("mbid"));
                    if(tracks.indexOf(track) < 0) {
                        //if(track.isValid()) {
                            tracks.add(track);
                        //}
                    }
                }
            }, pattern.toLowerCase(), limit, offset);
        } catch (Exception e) {e.printStackTrace();}
        return tracks;
    }

    public Artist getArtist(String id) {
        final List<Album> albums = new LinkedList <Album>();
        String queryArtist = "select * from simple_artist_info sai, (select mbid as a_mbid, name as a_name, artist_id from simple_album_info) as sel where sai.mbid=sel.artist_id and sai.mbid = ? limit 10";
        final Artist artist = new Artist();
        jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                artist.setName(resultSet.getString("name"));
                artist.setMbid(resultSet.getString("mbid"));
                albums.add(new Album(resultSet.getString("a_name"), resultSet.getString("a_mbid")));
            }
        }, UUID.fromString(id));
        artist.setAlbums(albums);
        return artist.isValid() ? artist : null;
    }

    public Album getAlbum(String id) {
        final List<Track> tracks = new LinkedList <Track>();
        String queryAlbum = "select * from simple_album_info sai, (select url as t_url, name as t_name, mbid as t_mbid, album_id from simple_track_info) as sel where sai.mbid = sel.album_id and sai.mbid = ?";
        final Album album = new Album();
        jdbcTemplate.query(queryAlbum, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                if(resultSet.wasNull()) return;
                album.setName(resultSet.getString("name"));
                album.setMbid(resultSet.getString("mbid"));
                tracks.add(new Track(resultSet.getString("t_name"), resultSet.getString("t_url"), resultSet.getString("t_mbid")));
            }
          }, UUID.fromString(id));
        album.setTracks(tracks);
        return album.isValid() ? album : null;
    }

    public Track getTrack(String id) {
        String queryTrack = ("select * from simple_track_info where mbid = ?");
        final Track track = new Track();
        jdbcTemplate.query(queryTrack, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                track.setName(resultSet.getString("name"));
                track.setMbid(resultSet.getString("mbid"));
                track.setUrl(resultSet.getString("url"));
            }
        }, UUID.fromString(id));
        return track.isValid() ? track : null;
    }

}