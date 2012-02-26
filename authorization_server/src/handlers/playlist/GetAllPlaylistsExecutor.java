package handlers.playlist;

import com.google.gson.Gson;
import handlers.RequestExecutor;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/12/12
 * Time: 1:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetAllPlaylistsExecutor extends RequestExecutor{
    @Override
    public Status execute(HttpServletResponse response, Map<String, String> parameters, CommonDbService dbService) {
        StringBuilder msg = new StringBuilder();
        String action = checkParameter("action", parameters, msg);
        if (action == null ||
                PlaylistHandler.ActionType.valueOf(action.toUpperCase()) != PlaylistHandler.ActionType.GETALL)
            return Status.NOT_ACCEPTED;
        Integer userId = getUserId(parameters, msg, dbService);
        if (userId == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        Gson gsonConverter = new Gson();
        String queryResult = gsonConverter.toJson(dbService.getPlaylistService().getAllPlaylists(userId));
        String callback = checkParameter("callback", parameters, new StringBuilder());
        if (callback != null) {
            msg.append(callback+"(");
            msg.append(queryResult);
            msg.append(");");
        } else
            msg.append(queryResult);
        try {
            response.getWriter().print(msg.toString());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Status.FAIL;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        return Status.SUCCESS;
    }
}
