package utilities;

import model.Playlist;
import model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/2/11
 * Time: 2:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommonDbService {
    private JdbcTemplate jdbcTemplate;

    private UserDbService userService;
    private PlaylistDbService playlistService;

    public CommonDbService() throws SQLException {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(
                "jdbc:mysql://localhost/ms_authorization",
                "root",
                "");
        driverManagerDataSource.setDriverClassName("org.gjt.mm.mysql.Driver");
        this.jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
        driverManagerDataSource.getConnection();
        userService = new UserDbService(jdbcTemplate);
        playlistService = new PlaylistDbService(jdbcTemplate);
    }

    public UserDbService getUserService() {
        return userService;
    }

    public PlaylistDbService getPlaylistService() {
        return playlistService;
    }
}
