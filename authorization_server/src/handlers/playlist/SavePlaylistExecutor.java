package handlers.playlist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import handlers.RequestExecutor;
import model.Playlist;
import utilities.CommonDbService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/12/12
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class SavePlaylistExecutor extends RequestExecutor{
    @Override
    public Status execute(HttpServletResponse response, Map<String, String> parameters, CommonDbService dbService) {
        StringBuilder msg = new StringBuilder();
        String action = checkParameter("action", parameters, msg);
        if (action == null ||
                PlaylistHandler.ActionType.valueOf(action.toUpperCase()) != PlaylistHandler.ActionType.SAVE)
            return Status.NOT_ACCEPTED;
        Integer userId = getUserId(parameters, msg, dbService);
        if (userId == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        String playlistTitle = checkParameter("name", parameters, msg);
        if (playlistTitle == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        boolean success = dbService.getPlaylistService().clearPlaylist(userId, playlistTitle);
        if (!success) {
            msg.append("Error: cannot save playlist. Such playlist does not exist\n");
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST,Status.FAIL);
        }
        String serializedEntries = checkParameter("entries", parameters, msg);
        if (serializedEntries == null)
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        Gson gsonConverter = new Gson();
        Type collectionType = new TypeToken<Collection<Playlist.Entry>>(){}.getType();
        Collection<Playlist.Entry> entries = null;
        entries = gsonConverter.fromJson(serializedEntries, collectionType);
        if (entries == null ) {
            msg.append("Error: cannot convert value of 'entries' parameter to JSON format\n");
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        }
        if (entries.isEmpty()) {
            msg.append("Error: no tracks were added for the playlist. Parameter 'entries' is empty\n");
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        }
        Integer playlistId = getPlaylistId(parameters, msg, dbService);
        if (playlistId == null) {
            return setErrorAndReturnStatus(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST, Status.FAIL);
        }
        int inserted = dbService.getPlaylistService().insertTrackBatch(playlistId, entries);
        success = (inserted == entries.size());
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            String callback = parameters.get("callback");
            if (callback != null)
                tryWrapMessageCallback(response, "saved", callback);
            else
                try {
                    response.getWriter().println("saved");
                } catch (IOException e) {
                    // nothing just return success status: this call does not require wrapper
                }
            return Status.SUCCESS;
        }
        msg.append("Error: Not all tracks were added: only "+inserted + " tracks were added\n");
        trySetErrorResponse(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST);
        return Status.FAIL;
    }
}
