import model.Track;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.List;
import java.util.Set;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/18/11
 * Time: 12:49 PM
 */
public class SearchHandler extends AbstractHandler
{
    SimpleDBConnection connection;
    public SearchHandler(SimpleDBConnection connection) {
        this.connection = connection;
    }

    public void handle(String s,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       int i) throws IOException, ServletException {
        httpServletResponse.setContentType("text/html;charset=utf-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        String pattern = httpServletRequest.getParameter("pattern");
        List<Track> tracks = connection.search(pattern);
        StringBuilder html = new StringBuilder();
        html.append("<body><table>" +
                "<th>Artist</th>" +
                "<th>Album</th>" +
                "<th>Track</th>" +
                "<th>URL</th>");
        for (Track track : tracks) {
            html.append("<tr>");
            html.append("<td>");
            html.append(track.getArtist());
            html.append("</td>");
            html.append("<td>");
            html.append(track.getAlbum());
            html.append("</td>");
            html.append("<td>");
            html.append(track.getName());
            html.append("</td>");
            html.append("<td>");
            html.append(track.getUrl());
            html.append("</td>");
            html.append("</tr>");
        }
        html.append("</table></body>");
        httpServletResponse.getWriter().println(html.toString());
        Request baseRequest = (httpServletRequest instanceof Request) ? (Request)httpServletRequest: HttpConnection.getCurrentConnection().getRequest();
        baseRequest.setHandled(true);
    }
}