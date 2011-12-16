package handlers;

import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.handler.AbstractHandler;
import utilities.DbConnectUtility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/8/11
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */

public class RegisterHandler extends AbstractHandler {

    private DbConnectUtility dbService;

    public RegisterHandler() throws SQLException {
        dbService = new DbConnectUtility();
    }

    public void handle(String s, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse, int i) throws IOException, ServletException {

        String username = (String)httpServletRequest.getParameter("username");
        String pwd = (String)httpServletRequest.getParameter("pwd");
        if (username == null || pwd == null || username.equals("") || pwd.equals("") ) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpServletResponse.getWriter().println("Username or password is not defined");
        } else {
            if (dbService.registerUser(username, pwd)) {
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                httpServletResponse.getWriter().println("Success!!!");
            } else {
                httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                httpServletResponse.getWriter().println("User with such name already exists");
            }
        }

        HttpConnection.getCurrentConnection().getRequest().setHandled(true);
    }
}
