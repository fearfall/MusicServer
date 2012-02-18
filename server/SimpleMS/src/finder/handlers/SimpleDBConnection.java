package finder.handlers;

import finder.model.*;
import finder.model.ResultCount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
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
    private static SimpleDBConnection simpleDBConnection;
    private Properties sqlProperties;
    private Map<Long, Result> searchCache = new HashMap<Long, Result>();

    public SimpleDBConnection() throws MusicServerException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");//:1521
        dataSource.setUrl("jdbc:postgresql://localhost/musicbrainz_db");
        dataSource.setUsername("musicbrainz");
        dataSource.setPassword("");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        sqlProperties = new Properties();
        try {
            sqlProperties.load(SimpleDBConnection.class.getResourceAsStream("../sql.properties"));
        } catch (IOException e) {
            throw new MusicServerException(e.getMessage());
        }
    }

    public Result search(String pattern, int offset, int limit) throws MusicServerException {
        long start = System.currentTimeMillis();

        SmallResult<Artist> artists = findArtists(pattern, offset, limit);
        SmallResult<Album> albums = findAlbums(pattern, offset, limit);
        SmallResult<Track> tracks = findTracks(pattern, offset, limit);
        Result result = new Result(artists, albums, tracks);

        return result.isValid() ? result : null;
    }

    public Result searchArtists(String pattern, int offset, int limit) throws MusicServerException {
        Result result = new Result(findArtists(pattern, offset, limit), null, null);
        return result.isValid() ? result : null;
    }

    public Result searchAlbums(String pattern, int offset, int limit) throws MusicServerException {
        Result result = new Result(null, findAlbums(pattern, offset, limit), null);
        return result.isValid() ? result : null;
    }

    public Result searchTracks(String pattern, int offset, int limit) throws MusicServerException {
        Result result = new Result(null, null, findTracks(pattern, offset, limit));
        return result.isValid() ? result : null;
    }

    public ResultCount getTotalAmount(String pattern) throws MusicServerException {
        return new ResultCount(getArtistsCount(pattern),
                               getAlbumsCount(pattern),
                               getTracksCount(pattern));
    }

    private int getArtistsCount (String pattern) throws MusicServerException {
        final int[] count = {0};
        String queryArtist = sqlProperties.getProperty("select.artist_count");
        try {
            jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    count[0] = Integer.parseInt(resultSet.getString("count"));
                }
            }, "%" + pattern.toLowerCase() + "%");
        } catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return count[0];
    }

    private int getAlbumsCount (String pattern) throws MusicServerException {
        final int[] count = {0};
        String queryArtist = sqlProperties.getProperty("select.album_count");
        try {
            jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    count[0] = Integer.parseInt(resultSet.getString("count"));
                }
            }, "%" + pattern.toLowerCase() + "%");
        } catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return count[0];
    }

    private int getTracksCount (String pattern) throws MusicServerException {
        final int[] count = {0};
        String queryTrack = sqlProperties.getProperty("select.track_count");
        try {
            jdbcTemplate.query(queryTrack, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    count[0] = Integer.parseInt(resultSet.getString("count"));
                }
            }, "%" + pattern.toLowerCase() + "%");
        } catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return count[0];
    }

    private SmallResult<Artist> findArtists(String pattern, int offset, int limit) throws MusicServerException {
        final List<Artist> artists = new ArrayList<Artist>();
        final Counter realOffset = new Counter(offset);
        String queryArtist = sqlProperties.getProperty("select.artists");
        try {
            jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    Artist artist = new Artist(resultSet.getString("name"), resultSet.getString("artist_mbid"));
                    realOffset.increase();
                    if(artists.indexOf(artist) < 0 && artist.isPartValid()) {
                        artists.add(artist);
                    }
                }
            }, "%" + pattern.toLowerCase() + "%", limit, offset);
        } catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return new SmallResult<Artist>(artists, realOffset.getValue());
    }

    private SmallResult<Album> findAlbums(String pattern, int offset, int limit) throws MusicServerException {
        final List<Album> albums = new ArrayList<Album>();
        final Counter realOffset = new Counter(offset);
        String queryAlbum = sqlProperties.getProperty("select.albums");
        try {
            jdbcTemplate.query(queryAlbum, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    Album album = new Album(resultSet.getString("album_name"),resultSet.getString("album_mbid"));
                    realOffset.increase();
                    if(albums.indexOf(album) < 0 && album.isPartValid()) {
                        albums.add(album);
                    }
                }
            }, "%" + pattern.toLowerCase() + "%", limit, offset);
        } catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return new SmallResult<Album>(albums, realOffset.getValue());
    }

    private SmallResult<Track> findTracks(String pattern, int offset, int limit) throws MusicServerException {
        final List<Track> tracks = new ArrayList<Track>();
        final Counter realOffset = new Counter(offset);
        String queryTrack = sqlProperties.getProperty("select.tracks");
        try {
            jdbcTemplate.query(queryTrack, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    Track track = new Track(resultSet.getString("track_name"), resultSet.getString("url"), resultSet.getString("track_mbid"));
                    realOffset.increase();
                    if (tracks.indexOf(track) < 0 && track.isValid()) {
                        tracks.add(track);
                    }
                }
            },  "%" + pattern.toLowerCase() + "%", limit, offset);
        } catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return new SmallResult<Track>(tracks, realOffset.getValue());
    }

    public Artist getArtist(String id) throws MusicServerException {
        final List<Album> albums = new LinkedList <Album>();
        //String queryArtist = "select * from simple_artist_info sai, (select mbid as a_mbid, name as a_name, artist_id from simple_album_info) as sel where sai.mbid=sel.artist_id and sai.mbid = ? limit 10";
        String queryArtist = sqlProperties.getProperty("select.artist_info");
        final Artist artist = new Artist();
        try {
            jdbcTemplate.query(queryArtist, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    artist.setName(resultSet.getString("name"));
                    artist.setMbid(resultSet.getString("artist_mbid"));
                    Album album = new Album(resultSet.getString("a_name"),resultSet.getString("a_mbid"));
                    if (albums.indexOf(album) < 0 && album.isPartValid()) {
                        albums.add(album);
                    }
                }
            }, UUID.fromString(id));
            artist.setAlbums(albums);
        }catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return artist.isAllValid() ? artist : null;
    }

    public Album getAlbum(String id) throws MusicServerException {
        final List<Track> tracks = new LinkedList <Track>();
        String queryAlbum = sqlProperties.getProperty("select.album_info");
        final Album album = new Album();
        try {
            jdbcTemplate.query(queryAlbum, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    if(resultSet.wasNull()) return;
                    album.setName(resultSet.getString("album_name"));
                    album.setMbid(resultSet.getString("album_mbid"));
                    Track track = new Track(resultSet.getString("t_name"), resultSet.getString("t_url"), resultSet.getString("t_mbid"));
                    if (tracks.indexOf(track) < 0 && track.isValid()) {
                        tracks.add(track);
                    }
                }
            }, UUID.fromString(id));
            album.setTracks(tracks);
        }catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return album.isAllValid() ? album : null;
    }

    public Track getTrack(String id) throws MusicServerException {
        String queryTrack = sqlProperties.getProperty("select.track_info");
        final Track track = new Track();
        try {
            jdbcTemplate.query(queryTrack, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    track.setName(resultSet.getString("track_name"));
                    track.setMbid(resultSet.getString("track_mbid"));
                    try {
                        track.checkUrl(resultSet.getString("url"), resultSet.getString("aid"),
                                              "317437", "76b4cf3676fbe9e076fbe9e0f376da3d08f76fb76fae9e804bf890503cbd8ca");
                    } catch (MusicServerException e) {
                        throw new SQLException(e.getMessage());
                    }

                }
            }, UUID.fromString(id));
        } catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
        return track.isValid() ? track : null;
    }

    public static SimpleDBConnection getInstance() throws MusicServerException {
        if(simpleDBConnection == null) {
            simpleDBConnection = new SimpleDBConnection();
        }
        return simpleDBConnection;
    }

    public void saveUrl(String mbid, String newUrl) throws MusicServerException {
        String updateTrack = sqlProperties.getProperty("update.track_url");
        try {
            jdbcTemplate.update(updateTrack, newUrl, UUID.fromString(mbid));
        }catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
    }

}
