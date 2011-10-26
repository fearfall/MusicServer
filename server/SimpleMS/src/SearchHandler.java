import com.google.gson.Gson;
import com.google.gson.JsonElement;
import model.Track;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.springframework.util.StringUtils;

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
        httpServletResponse.setContentType("text/plain;charset=utf-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        String pattern = httpServletRequest.getParameter("pattern");
        String jsonCallbackParam = httpServletRequest.getParameter("jsoncallback");
        List<Track> tracks = connection.search(pattern);
        StringBuilder html = new StringBuilder();
        /*html.append("<body><table>" +
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
        html.append("</table></body>");*/

        JsonElement jsonElement = new Gson().toJsonTree(tracks);
        if ( jsonCallbackParam != null ) {
            html.append(jsonCallbackParam);
            html.append("(");
            html.append(jsonElement);
            html.append(");");
        }
        else html.append(jsonElement);

        httpServletResponse.setContentLength(html.length());
        httpServletResponse.getWriter().println(html.toString());
        Request baseRequest = (httpServletRequest instanceof Request) ? (Request)httpServletRequest: HttpConnection.getCurrentConnection().getRequest();
        baseRequest.setHandled(true);
    }

    public static String sanitizeJsonpParam(String s) {
        if ( s.isEmpty()) return null;
        if ( !StringUtils.startsWithIgnoreCase(s,"jsonp")) return null;
        if ( s.length() > 128 ) return null;
        if ( !s.matches("^jsonp\\d+$")) return null;
        return s;
    }
}