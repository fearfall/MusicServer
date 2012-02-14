package utilities;

import model.Playlist;
import model.PlaylistList;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/11/12
 * Time: 7:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistDbService {
    private JdbcTemplate jdbcTemplate;

    public PlaylistDbService(JdbcTemplate jdbcTemplate) {
         this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<String> getAllPlaylists(final int userId) {
        String query  = ("select title from playlists where user_id = ? ");
        final List<String> playlists = new ArrayList<String>();
        jdbcTemplate.query(query, new RowCallbackHandler() {
                    public void processRow(ResultSet resultSet) throws SQLException {
                        playlists.add(resultSet.getString("title"));
                    }
                }, userId
        );
        return playlists;
    }

    public List<Playlist.Entry> getPlaylist(final int userId, final String playlist) {
        final List<Playlist.Entry> result = new ArrayList<Playlist.Entry>();
        try {
            Integer playlistId = getPlaylistId(userId, playlist);
            if (playlistId == -1 || playlistId == null) return null;
            String queryPlaylist = ("select order_num, entry_id, entry_data from playlist_entries where playlist_id = ?");
            jdbcTemplate.query(queryPlaylist, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                     result.add( new Playlist.Entry(
                             (String)resultSet.getString("entry_id"),
                             (String)resultSet.getString("entry_data"),
                             Integer.valueOf(resultSet.getString("order_num"))
                             ));
                }
            }, playlistId);
        } catch (EmptyResultDataAccessException e) {
            //nothing: just return empty list
        }
        return result;
    }

    public boolean createPlaylist(final int userId, final String title) {
        String query = ("insert into playlists (user_id, title) values (?, ?)");
        try {
            int res = jdbcTemplate.update(query, userId, title);
            return res > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public boolean deletePlaylist(final int userId, final String title) {
        String query = ("delete from playlists where user_id = ? and title = ?");
        try {
            int res = jdbcTemplate.update(query, userId, title);
            return res > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public boolean clearPlaylist(final int userId, final String title) {
        Integer playlistId = getPlaylistId(userId, title);
        if (playlistId == null) return false;
        String query = ("delete from playlist_entries where playlist_id = ?");
        int res = jdbcTemplate.update(query, playlistId);
        return true;
    }

    public Integer getPlaylistId(final int userId, final String title) {
        String queryPlaylistId = ("select id from playlists where user_id = ? and title = ?");
        Integer id = -1;
        try {
            id = jdbcTemplate.queryForInt(queryPlaylistId, userId, title);
        } catch (EmptyResultDataAccessException e) {
            // just empty  result
        } finally {
            return id;
        }
    }

    public boolean insertTrack(final int playlistId, int orderNum, final String mbid, final String data) {
        String query = ("insert into playlist_entries (playlist_id, entry_id, order_num, entry_data) values (?, ?, ?, ?)");
        try {
            int res = jdbcTemplate.update(query, playlistId, mbid, orderNum, data);
            return res > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public int insertTrackBatch(final int playlistId, final Collection<Playlist.Entry> entries) {
        StringBuilder query = new StringBuilder("insert into playlist_entries (playlist_id, entry_id, order_num, entry_data) values ");
        for (Playlist.Entry entry: entries) {
            query.append("(");
            query.append(playlistId + ", ");
            query.append("\"" + entry.mbid + "\", ");
            query.append(entry.order + ", ");
            query.append("\"" + entry.data + "\"");
            query.append("), ");
        }
        int lastComma = query.lastIndexOf(",");
        query.deleteCharAt(lastComma);
        return jdbcTemplate.update(query.toString());
    }

    public boolean deleteTrack(final int playlistId, int orderNum, final String mbid) {
        String query = ("delete from playlist_entries where playlist_id = ? and entry_id = ? and order_num = ?");
        try {
            int res = jdbcTemplate.update(query, playlistId, mbid, orderNum);
            return res > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }


}
