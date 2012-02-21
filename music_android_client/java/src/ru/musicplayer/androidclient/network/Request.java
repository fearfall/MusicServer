package ru.musicplayer.androidclient.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import ru.musicplayer.androidclient.model.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/1/11
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Request {
    private static SecureHttpClient mySecureHttpClient = null;
    private static String myIp = "192.168.1.3";
    private static String musicServerIp = "asande.no-ip.org";
    private static String authServerIp = myIp;
    private static String myServerAddress = "http://" + musicServerIp + ":6006";
    private static String myAuthorizationServerAddress = "https://" + authServerIp + ":8443";

    public static void setIp(String musicIp, String authIp) {
        //todo: validate ip
        musicServerIp = musicIp;
        authServerIp = authIp;
        myServerAddress = "http://" + musicServerIp + ":6006";
        myAuthorizationServerAddress = "https://" + authServerIp + ":8443";
    }

    public static String getMusicServerIp() {
        return musicServerIp;
    }

    public static String getAuthServerIp() {
        return authServerIp;
    }

    public static void init (String username, String password) {
        mySecureHttpClient = new SecureHttpClient(username, password, myIp);
    }

    private static HttpResponse execute (String request) throws IOException {
        if (request.contains("https://")) {
            if (mySecureHttpClient == null)
                throw new RuntimeException("You are unauthorized!");
            return mySecureHttpClient.execute(request);
        }
        return new DefaultHttpClient().execute(new HttpGet(request));
    }

    // requests to base server
    public static Model get (String type, String mbid) throws IOException {
        try {
            HttpResponse response = execute(myServerAddress + "/get/" + type + "?id=" + mbid);
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
                   // return new Gson().fromJson(r, (Type)Model.class);
                /*case HttpStatus.SC_NO_CONTENT:
                    return null;   */
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
            String offsetString = offset == 0 ? "" : "&offset=" + offset;
            HttpResponse response = execute(myServerAddress + "/search/?pattern=" + pattern + "&limit=" + limit + offsetString + "&type=" + contentType);
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
            HttpResponse response = execute(myServerAddress + "/count/?pattern=" + pattern);
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
            HttpResponse response = execute(myAuthorizationServerAddress + "/playlist/?action=" + action + "&name=" + playlistName);
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
            if (msg.contains("Connection to ") && msg.contains(myAuthorizationServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }

    /*public static String playlistAction(String action, String playlistName) throws IOException {
        URL url = new URL(myAuthorizationServerAddressSecure + "/playlist/?action=" + action + "&name=" + playlistName);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("authorization", "Basic "+ Base64.encode((myUsername + ':' + myPwd).getBytes()));
        connection.setRequestProperty("Own-Authentication-Form", "false");
        try {
            connection.setDoOutput(true);
            switch (connection.getResponseCode()) {
                case HttpStatus.SC_OK:
                    return "Playlist " + playlistName + " successfully " + action + 'd';
                default:
                    return connection.getResponseMessage();
            }
        }  catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myAuthorizationServerAddressSecure) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }  */



        /*BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
       String line;
       while ((line = in.readLine()) != null) {
           System.out.println(line);
       }
       in.close(); */
    //}

    //add, delete
    public static String trackAction (String action, String playlistName, int order, int mbid) throws IOException {
        try {
            HttpResponse response = execute(myAuthorizationServerAddress + "/playlist/?action=" +
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
            if (msg.contains("Connection to ") && msg.contains(myAuthorizationServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }

    public static PlaylistResult getPlaylist (String playlistName) throws IOException {
        try {
            HttpResponse response = execute(myAuthorizationServerAddress + "/playlist/?action=get&name=" + playlistName);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                Type collectionType = new TypeToken<List<PlaylistResultEntry>>(){}.getType();
                return new PlaylistResult((List<PlaylistResultEntry>) new Gson().fromJson(r, collectionType));
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
            if (msg.contains("Connection to ") && msg.contains(myAuthorizationServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }


    public static AllPlaylistsResult getAllPlaylists () throws IOException {
        try {
            HttpResponse response = execute(myAuthorizationServerAddress + "/playlist/?action=getall");
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                Type collectionType = new TypeToken<List<String>>(){}.getType();
                return new AllPlaylistsResult((List<String>) new Gson().fromJson(r, collectionType));
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
            if (msg.contains("Connection to ") && msg.contains(myAuthorizationServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }

    public static String register (String user, String pwd) throws IOException {
        try {
            init(user, pwd);
            HttpResponse response = execute(myAuthorizationServerAddress + "/register/?username=" + user + "&pwd=" + pwd);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                return "Registration successful!";
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                return statusLine.getReasonPhrase();
            }
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myAuthorizationServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }
    
    public static String login (String user, String pwd) throws IOException {
        try {
            init(user, pwd);
            HttpResponse response = execute(myAuthorizationServerAddress + "/login/?username=" + user + "&pwd=" + pwd);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                return "Login successful!";
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                return statusLine.getReasonPhrase();
            }
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myAuthorizationServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }
    
    public static String save(Playlist playlist) throws IOException {
        try {
            Type collectionType = new TypeToken<Collection<PlaylistResultEntry>>(){}.getType();
            HttpResponse response = execute(myAuthorizationServerAddress + "/playlist/?action=save&name=" + playlist.getName() + "&entries=" + new Gson().toJson(playlist.toEntryList(), collectionType));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                return "Playlist " + playlist.getName() + " successfully saved!";
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (HttpHostConnectException e2) {
            String msg = e2.getMessage();
            if (msg.contains("Connection to ") && msg.contains(myAuthorizationServerAddress) && msg.contains("refused")) {
                throw new IOException("Failed to connect to Music Authorization Server.\nCheck your internet connection.\nOr it may be Music Authorization Server Internal error ;)");
            }
            throw e2;
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }


}
