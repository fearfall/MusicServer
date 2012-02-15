package handlers.user;

import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/13/12
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginHandler extends AbstractHandler {
    public void handle(String s, HttpServletRequest request, HttpServletResponse response, int i) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("You are logged-in\n");
        HttpConnection.getCurrentConnection().getRequest().setHandled(true);
    }
}