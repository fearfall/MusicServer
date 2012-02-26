package handlers.user;

import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.handler.AbstractHandler;
import utilities.CommonDbService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/8/11
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */

public class RegisterHandler extends AbstractHandler {

    private CommonDbService dbService;

    public RegisterHandler(CommonDbService commonDbService) throws SQLException {
        this.dbService = commonDbService;
    }

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

    public enum ActionType { REGISTER, LOGIN, LOGOUT }

    public void handle(String s, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse, int i) {


        new RegisterExecutor().execute(
                httpServletResponse,
                getRequestParameters(httpServletRequest.getQueryString()),
                dbService
        );
        HttpConnection.getCurrentConnection().getRequest().setHandled(true);
    }
}
