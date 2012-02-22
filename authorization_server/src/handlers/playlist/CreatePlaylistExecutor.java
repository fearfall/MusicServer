package handlers.playlist;

import handlers.RequestExecutor;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/12/12
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreatePlaylistExecutor extends RequestExecutor{
    @Override
    public Status execute(HttpServletResponse response, Map<String, String> parameters, CommonDbService dbService) {
        StringBuilder msg = new StringBuilder();
        String action = checkParameter("action", parameters, msg);
        if (action == null ||
                PlaylistHandler.ActionType.valueOf(action.toUpperCase()) != PlaylistHandler.ActionType.CREATE)
            return Status.NOT_ACCEPTED;
        Integer userId = getUserId(parameters, msg, dbService);
        if (userId == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        String playlistTitle = checkParameter("name", parameters, msg);
        if (playlistTitle == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        boolean success = dbService.getPlaylistService().createPlaylist(userId, playlistTitle);
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
             String callback = parameters.get("callback");
            if (callback != null)
                tryWrapMessageCallback(response, "created", callback);
            else
                try {
                    response.getWriter().println("created");
                } catch (IOException e) {
                    // nothing just return success status: this call does not require wrapper
                }
            return Status.SUCCESS;
        }
        msg.append("Error: cannot create playlist. Such playlist already exists\n");
        trySetErrorResponse(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST);
        return Status.FAIL;
    }
}
