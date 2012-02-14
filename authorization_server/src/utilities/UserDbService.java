package utilities;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/11/12
 * Time: 8:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserDbService {
    private JdbcTemplate jdbcTemplate;

    public UserDbService(JdbcTemplate jdbcTemplate) {
         this.jdbcTemplate = jdbcTemplate;
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
}
