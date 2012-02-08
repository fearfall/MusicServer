package ru.musicplayer.androidclient.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import ru.musicplayer.androidclient.model.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/1/11
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Request {
    private static HttpClient myHttpClient = null;
    //private static String myServerAddress = "192.168.211.119";
    //private static String myServerAddress = "192.168.1.5";
    private static String myServerAddress = "192.168.1.2";
    private static String myAuthorizationServerAddress = myServerAddress;

    private static HttpResponse execute (String request) throws IOException {
        if (myHttpClient == null) {
            myHttpClient = new DefaultHttpClient();
        }
        return myHttpClient.execute(new HttpGet(request));
    }

    // requests to base server
    
    public static Model get (String type, String mbid) throws IOException {
        try {
            HttpResponse response = execute("http://" + myServerAddress + ":6006/get/" + type + "?id=" + mbid);
            StatusLine statusLine = response.getStatusLine();
            switch (statusLine.getStatusCode()) {
                case HttpStatus.SC_OK:
                    InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                    if (type.equals("artist"))
                        return new Gson().fromJson(r, (Type)Artist.class);
                    if (type.equals("album"))
                        return new Gson().fromJson(r, (Type)Album.class);
                    if (type.equals("track"))
                        return new Gson().fromJson(r, (Type) Track.class);
                    return new Gson().fromJson(r, (Type)Model.class);
                case HttpStatus.SC_NO_CONTENT:
                    return null;
                default:
                    //response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }

    public static Result search (String pattern, int limit, int offset, int contentType) throws IOException {
        try {
            HttpResponse response = execute("http://" + myServerAddress + ":6006/search/?pattern=" + pattern + "&limit=" + limit + "&offset=" + offset + "&type=" + contentType);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                return new Gson().fromJson(r, (Type)Result.class);
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (JsonSyntaxException e1) {
            if (e1.getMessage().contains("{ERROR}")) {
                return new Result();
            }
            throw e1;
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Server.\nCheck your internet connection.\nOr it may be Music Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }
    
    public static ResultCount getCount (String pattern) throws IOException {
        try {
            HttpResponse response = execute("http://" + myServerAddress + ":6006/count/?pattern=" + pattern);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                return new Gson().fromJson(r, (Type)ResultCount.class);
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (JsonSyntaxException e1) {
            if (e1.getMessage().contains("{ERROR}")) {
                return new ResultCount();
            }
            throw e1;
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Server.\nCheck your internet connection.\nOr it may be Music Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }
    
    //requests to authorization server
    
    //create, remove
    public static String playlistAction (String action, String playlistName) throws IOException {
        try {
            HttpResponse response = execute("http://" + myAuthorizationServerAddress + ":6009/playlist/?action=" + action + "&name=" + playlistName);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                return "Playlist " + playlistName + " successfully " + action + 'd';
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                return statusLine.getReasonPhrase();
            }
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }

    //add, delete
    public static String trackAction (String action, String playlistName, int order, int mbid) throws IOException {
        try {
            HttpResponse response = execute("http://" + myAuthorizationServerAddress + ":6009/playlist/?action=" +
                    action + "&name=" + playlistName + "&order=" + order + "&mbid=" + mbid);

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                return "Track was successfully " + action + (action.endsWith("d") ? "ed" : 'd');
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                return statusLine.getReasonPhrase();
            }
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }

    public static PlaylistResult getPlaylist (String playlistName) throws IOException {
        try {
            HttpResponse response = execute("http://" + myAuthorizationServerAddress + ":6009/playlist/?action=get&name=" + playlistName);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                 InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                 return new Gson().fromJson(r, (Type)PlaylistResult.class);
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                return new PlaylistResult(statusLine.getReasonPhrase());
            }
        } catch (JsonSyntaxException e1) {
            if (e1.getMessage().contains("{ERROR}")) {
                return new PlaylistResult("Music Authorization Server internal error");
            }
            throw e1;
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }


    public static AllPlaylistsResult getAllPlaylists () throws IOException {
        try {
            HttpResponse response = execute("http://" + myAuthorizationServerAddress + ":6009/playlist/?action=getall");
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                return new Gson().fromJson(r, (Type)AllPlaylistsResult.class);
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                return new AllPlaylistsResult(statusLine.getReasonPhrase());
            }
        } catch (JsonSyntaxException e1) {
            if (e1.getMessage().contains("{ERROR}")) {
                return new AllPlaylistsResult("Music Authorization Server internal error");
            }
            throw e1;
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }
}
