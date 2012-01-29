package finder.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import finder.model.Result;
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
        httpServletResponse.setContentType("application/json");
        String pattern = httpServletRequest.getParameter("pattern");
        String jsonCallbackParam = httpServletRequest.getParameter("jsoncallback");

        int offset = 0;
        String offsetParam = httpServletRequest.getParameter("offset");
        if(offsetParam != null) {
            offset = Integer.valueOf(offsetParam);
        }
        int limit = 10;
        String limitParam = httpServletRequest.getParameter("limit");
        if(offsetParam != null) {
            limit = Integer.valueOf(limitParam);
        }
        int resultContent = 0;
        String resTypeParam = httpServletRequest.getParameter("type");
        if(resTypeParam != null) {
            resultContent = Integer.valueOf(resTypeParam);
        }

        Result result;
        switch (resultContent) {
            case 3:
                result = connection.searchTracks(pattern, offset, limit);
                break;
            case 2:
                result = connection.searchAlbums(pattern, offset, limit);
                break;
            case 1:
                result = connection.searchArtists(pattern, offset, limit);
                break;
            default:
                //todo check everything (limit > 0 && limit is not very big and the same for offset)
                result = connection.search(pattern, offset, limit);
        }

        StringBuilder html = new StringBuilder();
        System.out.println(pattern);
        //html.append("<html> <head/> <body> ");
        if(result != null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            JsonElement jsonElement = new Gson().toJsonTree(result);
            if ( jsonCallbackParam != null ) {
                html.append(jsonCallbackParam);
                html.append("(");
                html.append(jsonElement);
                html.append(");");
            }
            else html.append(jsonElement);
        } else {
            //httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
            html.append("{ERROR}");
        }
        //html.append(" </body> </html>");
        //httpServletResponse.setContentLength(html.length());
        //httpServletResponse.setContentEncoding("gzip");
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
