package handlers;

import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 11/23/11
 * Time: 10:54 AM
 */
public class ErrorHandler extends AbstractHandler {
    public void handle(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int i) throws IOException, ServletException {
        httpServletResponse.setContentType("text/plain");
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        httpServletResponse.getWriter().println("INTERNAL SERVER ERROR");
        Request baseRequest = (httpServletRequest instanceof Request) ? (Request)httpServletRequest: HttpConnection.getCurrentConnection().getRequest();
        baseRequest.setHandled(true);
    }
}
