package handlers;

import com.google.gson.Gson;
import model.Playlist;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.handler.AbstractHandler;
import utilities.DbConnectUtility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/2/11
 * Time: 2:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistHandler extends AbstractHandler{
    private DbConnectUtility dbService;

    public PlaylistHandler() throws SQLException {
        dbService = new DbConnectUtility();
    }

    static enum ActionType {GET, GETALL, CREATE, REMOVE, ADD, DELETE}

    public void handle(String s, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse, int i) throws IOException, ServletException {

        boolean validRequest = true;
        String action = (String)httpServletRequest.getParameter("action");
        if (action == null || action.equals("")
                || ActionType.valueOf(action.toUpperCase()) == null) {
            validRequest = false;
            httpServletResponse.getWriter().println("action is not defined or is wrong defined");
        }
        ActionType actionType = ActionType.valueOf(action.toUpperCase());
        if (httpServletRequest.getUserPrincipal() == null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String username = httpServletRequest.getUserPrincipal().getName();
        Integer userId = Integer.valueOf(dbService.getUserIdByName(username));
        String title = "";
        if (actionType != ActionType.GETALL) {
            if (httpServletRequest.getParameter("name") != null) {
                title = (String)httpServletRequest.getParameter("name");
            } else {
                httpServletResponse.getWriter().println("Parameter name is not defined");
                validRequest = false;
            }
        }
        int orderNum = -1;
        String mbid = "";
        int playlistId = -1;
        if (actionType == ActionType.ADD || actionType == ActionType.DELETE) {
            if (httpServletRequest.getParameter("order") == null ||
                    httpServletRequest.getParameter("mbid") == null) {
                validRequest = false;
                httpServletResponse.getWriter().println("Parameters order or mbid are not defined");
            } else {
                orderNum = Integer.valueOf((String)httpServletRequest.getParameter("order"));
                mbid = (String)httpServletRequest.getParameter("mbid");
                playlistId = dbService.getPlaylistId(userId, title);
                if (playlistId == -1) {
                    validRequest = false;
                    httpServletResponse.getWriter().println("User doesn`t have a playlist with such name");
                }
            }
        }
        if (validRequest) {
            Gson gsonConverter = new Gson();
            switch (actionType) {
                case ADD:
                    if (dbService.insertTrack(playlistId,orderNum, mbid)) {
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                        httpServletResponse.getWriter().println("inserted");// !!!!!!!!!!!
                    } else {
                        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        httpServletResponse.getWriter().println("Such track already exists");
                    }
                    break;
                case DELETE:
                    if (dbService.deleteTrack(playlistId,orderNum, mbid)) {
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                        httpServletResponse.getWriter().println("deleted");// !!!!!!!!!!!
                    } else {
                        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        httpServletResponse.getWriter().println("Such track doesn`t exist");
                    }
                    break;
                case GET:
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    httpServletResponse.getWriter().println(gsonConverter.toJson(dbService.getPlaylist(userId, title)));
                    break;
                case GETALL:
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    httpServletResponse.getWriter().println(gsonConverter.toJson(dbService.getAllPlaylists(userId)));
                    break;
                case CREATE:
                    if (dbService.createPlaylist(userId, title)) {
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                        httpServletResponse.getWriter().println("inserted");// !!!!!!!!!!!
                    } else {
                        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        httpServletResponse.getWriter().println("Such playlist already exists");
                    }
                    break;
                case REMOVE:
                    if (dbService.deletePlaylist(userId, title)) {
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                        httpServletResponse.getWriter().println("deleted");// !!!!!!!!!!!
                    } else {
                        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        httpServletResponse.getWriter().println("Such playlist doesn`t exist");
                    }
                    break;
            }
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        HttpConnection.getCurrentConnection().getRequest().setHandled(true);

    }
}
