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

    public List<Track> search(String pattern) {
        //final Map<Track, String> urls = new HashMap<Track, String>();
        final List<Track> urls = new ArrayList<Track>();
        String query = ("select * from all_tracks_info where lower(artist) like \'%\' || ? || \'%\'");
        jdbcTemplate.query(query, new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                urls.add(new Track(resultSet.getString("track"),
                        resultSet.getString("artist"),
                        resultSet.getString("release"),
                        resultSet.getString("url")));
            }
        }, pattern.toLowerCase());

        return urls;
    }

}
