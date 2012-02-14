package handlers.playlist;

import com.google.gson.Gson;
import handlers.RequestExecutor;
import model.Playlist;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/12/12
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetPlaylistExecutor extends RequestExecutor{
    @Override
    public Status execute(HttpServletResponse response, Map<String, String> parameters, CommonDbService dbService) throws IOException {
        StringBuilder msg = new StringBuilder();
        String action = checkParameter("action", parameters, msg);
        if (action == null ||
                PlaylistHandler.ActionType.valueOf(action.toUpperCase()) != PlaylistHandler.ActionType.GET)
            return Status.NOT_ACCEPTED;
        Integer userId = getUserId(parameters, msg, dbService);
        if (userId == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        String playlistTitle = checkParameter("name", parameters, msg);
        if (playlistTitle == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        Gson gsonConverter = new Gson();
        List<Playlist.Entry> queryResult = dbService.getPlaylistService().getPlaylist(userId, playlistTitle);
        if (queryResult == null)
            return setErrorAndReturnStatus(
                    response,
                    "Such playlist doesn`t exist",
                    HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        String serializedResult = gsonConverter.toJson(queryResult);
        String callback = checkParameter("callback", parameters, new StringBuilder());
        if (callback != null) {
            msg.append(callback+"(");
            msg.append(serializedResult);
            msg.append(");");
        } else
            msg.append(serializedResult);
        response.getWriter().print(msg.toString());
        response.setStatus(HttpServletResponse.SC_OK);
        return Status.SUCCESS;
    }
}
