package handlers.playlist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.MalformedJsonException;
import handlers.RequestExecutor;
import model.Playlist;
import utilities.CommonDbService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import handlers.playlist.PlaylistHandler.ActionType;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/11/12
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddTrackBatchExecutor extends RequestExecutor{
    @Override
    public Status execute(
            HttpServletResponse response,
            final Map<String, String> parameters,
            CommonDbService dbService) throws IOException {

        StringBuilder msg = new StringBuilder();
        String action = checkParameter("action", parameters, msg);
        if ( action == null ||
                (action != null && ActionType.valueOf(action.toUpperCase())!= ActionType.ADD_BATCH))
            return Status.NOT_ACCEPTED;

        boolean success = true;
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
            return Status.SUCCESS;
        }
        msg.append("Error: Not all tracks were added: only "+inserted + " tracks were added\n");
        setErrorResponse(response, msg.toString(), HttpServletResponse.SC_BAD_REQUEST);
        return Status.FAIL;
    }
}
