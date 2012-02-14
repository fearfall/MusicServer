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
 * Time: 12:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteTrackExecutor extends RequestExecutor{
    @Override
    public Status execute(HttpServletResponse response, Map<String, String> parameters, CommonDbService dbService) throws IOException {
        StringBuilder msg = new StringBuilder();
        String action = checkParameter("action", parameters, msg);
        if (action == null ||
                ActionType.valueOf(action.toUpperCase())!= ActionType.DELETE)
            return Status.NOT_ACCEPTED;
        Integer playlistId = getPlaylistId(parameters, msg, dbService);
        if (playlistId == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        String orderValue = checkParameter("order", parameters, msg);
        if (orderValue == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        Integer orderNum = Integer.valueOf(orderValue);
        if (orderNum == null) {
            msg.append("Error: bad parameter 'order'\n");
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        }
        String mbid = checkParameter("mbid", parameters, msg);
        if (mbid == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        boolean success = dbService.getPlaylistService().deleteTrack(playlistId, orderNum, mbid);
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            return Status.SUCCESS;
        }
        msg.append("Error: track "+mbid+" wasnot deleted\n");
        setErrorResponse(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST);
        return Status.FAIL;
    }
}
