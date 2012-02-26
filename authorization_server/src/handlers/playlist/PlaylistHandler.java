package handlers.playlist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import handlers.RequestExecutor;
import model.Playlist;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.handler.AbstractHandler;
import utilities.CommonDbService;
import utilities.PlaylistDbService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import handlers.RequestExecutor.Status;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/2/11
 * Time: 2:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistHandler extends AbstractHandler{
    private CommonDbService dbService;

    public PlaylistHandler(CommonDbService commonDbService) throws SQLException {
        dbService = commonDbService;
    }

    static enum ActionType {GET, GETALL, CREATE, REMOVE, SAVE, ADD, ADD_BATCH, CLEAR, DELETE}

    private Map<String, String> getRequestParameters(String queryString) {
        Map<String, String> parameters = new HashMap<String, String>();
        String[] values = queryString.split("&");
        for (String value: values) {
            int firstDelimiter = value.indexOf("=");
            if (firstDelimiter > 0) {
                String paramName = value.substring(0, firstDelimiter);
                String paramValue = value.substring(firstDelimiter+1);
                parameters.put(paramName, paramValue);
            }
        }
        return parameters;
    }

    public void handle(String s, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse, int i) {
        String query = httpServletRequest.getQueryString();
        Map<String, String> parameters = getRequestParameters(query);
        String username = httpServletRequest.getUserPrincipal().getName();
        parameters.put("username", username);
        Status resultStatus = PlaylistExecutors.tryProcessRequest(parameters, httpServletResponse, dbService);
        if (resultStatus == Status.NOT_ACCEPTED) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                httpServletResponse.getWriter().println("Error: nothing was done. Server cannot process this request");
            } catch (IOException e) {
                //nothing: just set status bad request
            }
        }
        HttpConnection.getCurrentConnection().getRequest().setHandled(true);

    }
}
