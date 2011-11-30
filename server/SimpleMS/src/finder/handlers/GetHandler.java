package finder.handlers;

import com.google.gson.Gson;
//import finder.handlers.SimpleDBConnection;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 11/05/11
 * Time: 11:23 AM
 */
public class GetHandler extends AbstractHandler
{
    SimpleDBConnection connection;

    public GetHandler(SimpleDBConnection connection) {
        this.connection = connection;
    }

    public void handle(String s,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       int i) throws IOException, ServletException {

        httpServletResponse.setContentType("application/json");

        String id = httpServletRequest.getParameter("id");
        String jsonCallbackParam = httpServletRequest.getParameter("jsoncallback");
        Object result = null;
        System.out.println(id);
        StringBuilder html = new StringBuilder();
        if(s.equals("/artist")) {
            result = connection.getArtist(id);
        }
        if(s.equals("/album")) {
            result = connection.getAlbum(id);
        }
        if(s.equals("/track")) {
            result = connection.getTrack(id);
        }
        //html.append("<html> <head/> <body>");
        if(result != null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            String jsonElement = new Gson().toJson(result);
            if (jsonCallbackParam != null) {
                html.append(jsonCallbackParam);
                html.append("(");
                html.append(jsonElement);
                html.append(");");
            }
            else html.append(jsonElement);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
            html.append("{ERROR}");
        }
        //html.append(" </body> </html>");
        //httpServletResponse.setContentLength(html.length());
        System.out.println(html.toString());
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
