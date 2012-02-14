package handlers.playlist;

import handlers.RequestExecutor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import handlers.playlist.PlaylistHandler.ActionType;
import handlers.RequestExecutor.Status;
import utilities.CommonDbService;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/11/12
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistExecutors {
    public static List<RequestExecutor> executors = new ArrayList<RequestExecutor>();
    static {
        executors.add(new AddPlaylistExecutor());
        executors.add(new AddTrackBatchExecutor());
        executors.add(new DeleteTrackExecutor());
        executors.add(new ClearPlaylist());
        executors.add(new GetAllPlaylistsExecutor());
        executors.add(new GetPlaylistExecutor());
        executors.add(new CreatePlaylistExecutor());
        executors.add(new RemovePlaylistExecutor());
        executors.add(new SavePlaylistExecutor());
    }

    public static Status tryProcessRequest(
            final Map<String, String> parameters,
            HttpServletResponse response,
            CommonDbService dbService) throws IOException {
        Status resultStatus = Status.NOT_ACCEPTED;
        int i = 0;
        while (i < executors.size() && resultStatus == Status.NOT_ACCEPTED) {
            resultStatus = executors.get(i).execute(response, parameters, dbService);
            ++i;
        }
        return resultStatus;
    }
}
