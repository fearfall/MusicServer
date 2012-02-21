package finder.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import finder.model.Result;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.servlet.SessionHandler;
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

public class SearchHandler extends /*SessionHandler//*/AbstractHandler
{
    /*public SearchHandler(SessionManager sessionManager) {
        super(sessionManager);
    }*/

    public SearchHandler() {
    }

    public void handle(String s,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       int i) throws IOException, ServletException {
        final StringBuilder html = new StringBuilder();
        httpServletResponse.setContentType("application/json");
        String jsonCallbackParam = null;
        try {
            jsonCallbackParam = getParameter(httpServletRequest,"jsoncallback");
        } catch (MusicServerException e) { }
        String htmlContent;
        try {
        String pattern = getParameter(httpServletRequest, "pattern");
            System.out.println("PATTERN IS: " + pattern);
        int limit = Integer.valueOf(getParameter(httpServletRequest, "limit"));
        int offset = Integer.valueOf(getParameter(httpServletRequest, "offset"));
        int resultContent = Integer.valueOf(getParameter(httpServletRequest, "type"));
        Result result;
        switch (resultContent) {
        case 3:
            result = SimpleDBConnection.getInstance().searchTracks(pattern, offset, limit);
            break;
        case 2:
            result = SimpleDBConnection.getInstance().searchAlbums(pattern, offset, limit);
            break;
        case 1:
            result = SimpleDBConnection.getInstance().searchArtists(pattern, offset, limit);
            break;
        default:
            result = SimpleDBConnection.getInstance().search(pattern, offset, limit);
        }
        if(result != null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            JsonElement jsonElement = new Gson().toJsonTree(result);
            System.out.println(jsonElement);
            htmlContent = jsonElement.toString();
        }
        else {
            //httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
            htmlContent = "[]";
        }
        } catch(MusicServerException e) {
           // httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            htmlContent = "{\"Error\": \"" + e.getMessage() + "\"}";
        }

        if ( jsonCallbackParam != null ) {
            html.append(jsonCallbackParam);
            html.append("(");
            html.append(htmlContent);
            html.append(");");
        }
        else html.append(htmlContent);
        System.out.println(html);
        httpServletResponse.getWriter().println(html.toString());
        Request baseRequest = (httpServletRequest instanceof Request) ? (Request)httpServletRequest: HttpConnection.getCurrentConnection().getRequest();
        baseRequest.setHandled(true);
    }


    private String getParameter (HttpServletRequest httpServletRequest, String name) throws MusicServerException {
        String result = httpServletRequest.getParameter(name);
        if("pattern".equals(name)) {
            if(result == null)
                throw new MusicServerException("No pattern speciefied");
            return validatePattern(result);
        }
        if("offset".equals(name)) {
            if(result == null)
                return "0";
            return validateOffset(result);
        }
        if("jsoncallback".equals(name)) {
            return result;//validateCallback(result);
        }
        if("limit".equals(name)) {
            if(result == null)
                return "15";
            return validateLimit(result);
        }
        if("type".equals(name)) {
            if(result == null)
                return "0";
            return result;
        }
        return result;
    }

    private String validateLimit(String result) throws MusicServerException {
        if (result != null) {
            Integer integer = Integer.valueOf(result);
            if(!(integer > 0 && integer < Integer.MAX_VALUE))
                throw new MusicServerException("Wrong limit values");
            else 
                return result;
        }
        return "0";
    }


    private String validateOffset(String result) throws MusicServerException {
        if (result != null) {
            Integer integer = Integer.valueOf(result);
            if(!(integer > 0 && integer < Integer.MAX_VALUE))
                throw new MusicServerException("Wrong offset values");
            else
                return result;
        }
        return "0";
    }

    private String validatePattern(String pattern) throws MusicServerException {
        if("".equals(pattern.trim()) || pattern.contains("%"))
            throw new MusicServerException("Wrong pattern");
        else
            return pattern;
    }

    public String validateCallback(String s) throws MusicServerException {
        boolean res = ( !StringUtils.startsWithIgnoreCase(s,"jsonp")) ||
        ( s.length() > 128 ) ||
        ( !s.matches("^jsonp\\d+$"));
        if(res)
            throw new MusicServerException("Wrong callback");
        else
            return s;
    }
    

}
