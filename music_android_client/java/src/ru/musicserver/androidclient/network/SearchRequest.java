package ru.musicserver.androidclient.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;

import org.apache.http.util.EntityUtils;
import ru.musicserver.androidclient.model.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/1/11
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequest {
    public static Result search (String pattern) throws IOException {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet("http://192.168.1.2:6006/search/?pattern=" + pattern));
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

            /*Artist artist = new Artist("artist1", "artist_mbid1");
            artist.addAlbum(new Album("album1", "album_mbid1"));
            artist.addTrack(new Track("track1", "/home/kate/test.mp3", "track_mbid1"));

            ArrayList<Artist> artists = new ArrayList<Artist>();
            artists.add(artist);

            ArrayList<Album> albums = new ArrayList<Album>();
            ArrayList<Track> tracks = new ArrayList<Track>();
            tracks.add(new Track("track1", "/home/kate/test.mp3", "track_mbid1"));
            return new Result(artists, albums, tracks); */

        }
    }
}
