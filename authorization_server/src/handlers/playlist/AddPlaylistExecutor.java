package handlers.playlist;

import handlers.RequestExecutor;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/11/12
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddPlaylistExecutor extends RequestExecutor {
    @Override
    public Status execute(
            HttpServletResponse response,
            final Map<String, String> parameters,
            CommonDbService dbService) throws IOException {
        StringBuilder msg = new StringBuilder();
        String action = checkParameter("action", parameters, msg);
        if ( action == null ||
                (action != null && PlaylistHandler.ActionType.valueOf(action.toUpperCase())!= PlaylistHandler.ActionType.ADD))
            return Status.NOT_ACCEPTED;
        Integer playlistId = getPlaylistId(parameters, msg, dbService);
        if (playlistId == null) {
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        }
        String trackData = checkParameter("data",parameters, msg);
        Integer orderNum = Integer.valueOf(checkParameter("order", parameters, msg));
        String mbid = checkParameter("mbid", parameters, msg);
        if (trackData==null || playlistId==null || orderNum==null || mbid==null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        boolean success = dbService.getPlaylistService().insertTrack(playlistId, orderNum, mbid, trackData);
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            String callback = parameters.get("callback");
            if (callback != null)
                wrapMessageCallback(response, "added", callback);
            else
                response.getWriter().println("added");
            return Status.SUCCESS;
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(msg.toString());
            return Status.FAIL;
        }

    }
}
