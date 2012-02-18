package handlers.playlist;

import handlers.RequestExecutor;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import handlers.playlist.PlaylistHandler.ActionType;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/12/12
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClearPlaylist extends RequestExecutor{
    @Override
    public Status execute(HttpServletResponse response, Map<String, String> parameters, CommonDbService dbService) throws IOException {
        StringBuilder msg = new StringBuilder();
        String action = checkParameter("action", parameters, msg);
        if (action == null ||
                ActionType.valueOf(action.toUpperCase()) != ActionType.CLEAR)
            return Status.NOT_ACCEPTED;
        Integer userId = getUserId(parameters, msg, dbService);
        if (userId == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        String playlistTitle = checkParameter("name", parameters, msg);
        if (playlistTitle == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        boolean success = dbService.getPlaylistService().clearPlaylist(userId, playlistTitle);
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
             String callback = parameters.get("callback");
            if (callback != null)
                wrapMessageCallback(response, "cleared", callback);
            else
                response.getWriter().println("cleared");
            return Status.SUCCESS;
        }
        msg.append("Error: cannot clear playlist. Such playlist does not exist\n");
        setErrorResponse(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST);
        return Status.FAIL;
    }
}
