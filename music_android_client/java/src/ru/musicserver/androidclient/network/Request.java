package ru.musicserver.androidclient.network;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import ru.musicserver.androidclient.model.Album;
import ru.musicserver.androidclient.model.Artist;
import ru.musicserver.androidclient.model.Model;
import ru.musicserver.androidclient.model.Result;

import java.io.File;
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
    private static String myServerAddress = "192.168.1.3";

    private static HttpResponse execute (String request) throws IOException {
        if (myHttpClient == null) {
            myHttpClient = new DefaultHttpClient();
        }
        /*if (myServerAddress == null) {
            File propertiesFile = new File(".");
            myServerAddress = "192.168.1.3";//"192.168.211.119";

        }*/
        return myHttpClient.execute(new HttpGet(request));
    }

    public static Model get (String type, String mbid) throws IOException {
        try {
            HttpResponse response = execute("http://" + myServerAddress + ":6006/get/" + type + "?id=" + mbid);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                if (type.equals("artist"))
                    return new Gson().fromJson(r, (Type)Artist.class);
                if (type.equals("album"))
                    return new Gson().fromJson(r, (Type)Album.class);
                return new Gson().fromJson(r, (Type)Model.class);

            } else {
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }

    public static Result search (String pattern) throws IOException {
        try {
            HttpResponse response = execute("http://" + myServerAddress + ":6006/search/?pattern=" + pattern);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                InputStreamReader r = new InputStreamReader(response.getEntity().getContent());
                return new Gson().fromJson(r, (Type)Result.class);
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            throw new IOException("Connection ERROR: " + e.getMessage());
        }
    }
}
