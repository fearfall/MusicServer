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
    public GetHandler() {
    }

    public void handle(String s,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       int i) throws IOException, ServletException {

        httpServletResponse.setContentType("application/json");
        StringBuilder html = new StringBuilder();
        String error = "";
        try {
            String id = httpServletRequest.getParameter("id");
            if(id == null)
                throw new MusicServerException("No entity specified");
            String jsonCallbackParam = httpServletRequest.getParameter("jsoncallback");
            Object result = null;
            if(s.equals("/artist")) {
                result = SimpleDBConnection.getInstance().getArtist(id);
            }
            if(s.equals("/album")) {
                result = SimpleDBConnection.getInstance().getAlbum(id);
            }
            if(s.equals("/track")) {
                result = SimpleDBConnection.getInstance().getTrack(id);
            }

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
            }
        } catch (MusicServerException e) {
            html.append(e.getMessage());
        }                             
        
        System.out.println(html.toString());
        httpServletResponse.getWriter().println(html.toString());
        Request baseRequest = (httpServletRequest instanceof Request) ? (Request)httpServletRequest: HttpConnection.getCurrentConnection().getRequest();
        baseRequest.setHandled(true);
    }
}
