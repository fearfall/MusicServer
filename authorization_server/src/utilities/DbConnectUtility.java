package utilities;

import model.Playlist;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/2/11
 * Time: 2:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class DbConnectUtility {
    private JdbcTemplate jdbcTemplate;

    public DbConnectUtility() throws SQLException {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(
                "jdbc:mysql://localhost/ms_authorization",
                "root",
                "Ghbrjk!2");
        driverManagerDataSource.setDriverClassName("org.gjt.mm.mysql.Driver");
        this.jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
        driverManagerDataSource.getConnection();
    }

    public String getUserIdByName(final String username) {
        String query = ("select * from users where username = ? ");
        final String[] id = new String[1];
        jdbcTemplate.query(query, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                id[0] = resultSet.getString("id");
            }
        }, username);
        return id[0];
    }

    private boolean checkExistUser(final String username) {
        String checkQuery = ("Select * from users where username = ? ");
        final String[] users = new String[1];
        users[0] = null;
        jdbcTemplate.query(checkQuery, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                users[0] = resultSet.getString("username");
            }
        }, username);
        return users[0] != null;
    }

    public boolean registerUser(final String username, final String pwd) {
        String query = ("insert into users(username, pwd) values ");
        String addRoleQuery = ("insert into user_roles(user_id, role_id) values ");
        if (!checkExistUser(username)) {
            query = query.concat("(\""+username+"\",\""+pwd+"\")");
            jdbcTemplate.execute(query);
            String newUserId = getUserIdByName(username);
            addRoleQuery = addRoleQuery.concat("("+newUserId+","+"1)");
            jdbcTemplate.execute(addRoleQuery);
            return true;
        }
        return false;
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
        String queryPlaylistId = ("select id from playlists where user_id = ? and title = ?");
        final List<Playlist.Entry> result = new ArrayList<Playlist.Entry>();
        try {
            Integer playlistId = jdbcTemplate.queryForInt(queryPlaylistId, userId, playlist);

            if (playlistId == null) return result;
            String queryPlaylist = ("select order_num, entry_id from playlist_entries where playlist_id = ?");
            jdbcTemplate.query(queryPlaylist, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                     result.add(
                             new Playlist.Entry((String)resultSet.getString("entry_id"),
                             Integer.valueOf(resultSet.getString("order_num"))));
                }
            }, playlistId);
        } catch (EmptyResultDataAccessException e) {
            //to do?
        } finally {
            return result;
        }
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

    public boolean insertTrack(final int playlistId, int orderNum, final String mbid) {
        String query = ("insert into playlist_entries (playlist_id, entry_id, order_num) values (?, ?, ?)");
        try {
            int res = jdbcTemplate.update(query, playlistId, mbid, orderNum);
            return res > 0;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
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
