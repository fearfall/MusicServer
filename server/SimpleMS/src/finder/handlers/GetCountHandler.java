package finder.handlers;

import com.google.gson.Gson;
import finder.model.ResultCount;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 28/01/12
 * Time: 21:13
 */
public class GetCountHandler extends AbstractHandler {
    public GetCountHandler() {}

    public void handle(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int i) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json");
        String pattern = httpServletRequest.getParameter("pattern");
        String jsonCallbackParam = httpServletRequest.getParameter("jsoncallback");
        StringBuilder html = new StringBuilder();
        ResultCount result;
        try {
            result = SimpleDBConnection.getInstance().getTotalAmount(pattern);
        if(result != null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            String jsonElement = new Gson().toJson(result);
            if (jsonCallbackParam != null) {
                html.append(jsonCallbackParam);
                html.append("(");
                html.append(jsonElement);
                html.append(");");
            } else
                html.append(jsonElement);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        } catch (MusicServerException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            html.append("{" + e.getMessage() + "}");
        }
        httpServletResponse.getWriter().println(html.toString());
        Request baseRequest = (httpServletRequest instanceof Request) ? (Request)httpServletRequest: HttpConnection.getCurrentConnection().getRequest();
        baseRequest.setHandled(true);
    }
}
