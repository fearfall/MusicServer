package handlers;

import sun.rmi.runtime.Log;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/11/12
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RequestExecutor {
    public enum Status { SUCCESS, FAIL, NOT_ACCEPTED}

    public abstract Status execute(
            HttpServletResponse response,
            final Map<String, String> parameters,
            CommonDbService dbService) throws IOException;

    public String checkParameter(
            final String parameterName,
            final Map<String, String> parameters,
            StringBuilder msg) {

        String value = parameters.get(parameterName);
        if (value == null) {
            msg.append("Error: parameter " + parameterName + " is missed\n");
        }
        return value;
    }

    public Integer getUserId(final Map<String, String> parameters, StringBuilder msg, CommonDbService dbService) {
        String username = checkParameter("username", parameters, msg);
        Integer userId = null;
        if (username != null)
            userId = Integer.valueOf(dbService.getUserService().getUserIdByName(username));
        return  userId;
    }

    public Integer getPlaylistId(final Map<String, String> parameters, StringBuilder msg, CommonDbService dbService) {
        Integer userId = getUserId(parameters, msg, dbService);
        Integer playlistId = null;
        String playlistTitle = checkParameter("name", parameters, msg);
        if (userId != null && playlistTitle != null)
            playlistId = dbService.getPlaylistService().getPlaylistId(userId, playlistTitle);
        return playlistId;
    }

    public void setErrorResponse(HttpServletResponse response, final String errorMessage, int status) {
        try {
            response.sendError(status, errorMessage);
        } catch (IOException e) {
            //nothing just set error status
            response.setStatus(status);
        }
    }

    public Status setErrorAndReturnStatus(HttpServletResponse response, final String errorMessage,
                                          int responseStatus, Status forReturn) {
        setErrorResponse(response, errorMessage, responseStatus);
        return forReturn;
    }
}
