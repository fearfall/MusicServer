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
 * To change this template use File | Settings | File Templates.
 */
public class GetCountHandler extends AbstractHandler {
    public GetCountHandler() {}

    public void handle(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int i) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json");
        String pattern = httpServletRequest.getParameter("pattern");
        String jsonCallbackParam = httpServletRequest.getParameter("jsoncallback");

        ResultCount result = SimpleDBConnection.getInstance().getTotalAmount(pattern);

        StringBuilder html = new StringBuilder();
        //html.append("<html> <head/> <body>");
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
            html.append("{ERROR}");
        }
        //html.append(" </body> </html>");
        //httpServletResponse.setContentLength(html.length());
        System.out.println(html.toString());
        httpServletResponse.getWriter().println(html.toString());
        Request baseRequest = (httpServletRequest instanceof Request) ? (Request)httpServletRequest: HttpConnection.getCurrentConnection().getRequest();
        baseRequest.setHandled(true);
    }
}
