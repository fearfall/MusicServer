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
        if ((parameters.containsKey("action")
                && PlaylistHandler.ActionType.valueOf(parameters.get("action").toUpperCase()) == PlaylistHandler.ActionType.ADD))
            {
                StringBuilder msg = new StringBuilder();
                boolean success = true;

                String username = checkParameter("username", parameters, msg);
                Integer userId = null;
                if (username != null)
                    userId = Integer.valueOf(dbService.getUserService().getUserIdByName(username));

                Integer playlistId = null;
                String playlistTitle = checkParameter("name", parameters, msg);
                if (userId != null && playlistTitle != null)
                    playlistId = dbService.getPlaylistService().getPlaylistId(userId, playlistTitle);

                String trackData = checkParameter("data",parameters, msg);
                Integer orderNum = Integer.valueOf(checkParameter("order", parameters, msg));
                String mbid = checkParameter("mbid", parameters, msg);

                if (trackData==null || playlistId==null || orderNum==null || mbid==null) {
                    success = false;
                } else {
                    success = dbService.getPlaylistService().insertTrack(playlistId, orderNum, mbid, trackData);
                }
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    return Status.SUCCESS;
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println(msg.toString());
                    return Status.FAIL;
                }
            } else
                return Status.NOT_ACCEPTED;
    }
}
