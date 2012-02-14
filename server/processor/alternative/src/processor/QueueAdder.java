package processor;

import com.google.gson.*;
import model.Track;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 2/2/12
 * Time: 7:25 AM
 */
class Item {
    private String name;
    private String mbid;

    Item() {
    }

    Item(String name, String mbid) {
        this.name = name;
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }
}
public class QueueAdder implements Runnable{
    private ExecutorService service = Executors.newFixedThreadPool(10);
    private static Logger LOG = Logger.getLogger(QueueAdder.class);

   /* private ConcurrentLinkedQueue<Track> queue;

    public QueueAdder(ConcurrentLinkedQueue<Track> queue) {
        this.queue = queue;
    }*/

    public QueueAdder() {
        ConsoleAppender trackConsoleAppender = new ConsoleAppender(new PatternLayout("%d [%t] %-5p %c %x - %m%n"));
        trackConsoleAppender.setName("queueAdderConsoleAppender");
        if(LOG.getAppender("queueAdderAppender") == null)
            LOG.addAppender(trackConsoleAppender);
    }

    public void run() {
        List<Item> artists = getTopArtists();
        for(Item artist : artists) {
            Map<String, String> albums = getTopArtistsAlbums(artist);
            List<Track> tracks = DBGrabber.getInstance().getTracks(artist);
            for(Track track : tracks) {
                if(albums.containsKey(track.getAlbumMbid())) {// && albums.containsValue(track.getAlbumName())){
                    LOG.info("ADDING TRACK: " + track.getName());
                    service.execute(new TrackProcessor(track));
                }
            }
        }
    }

    void submitTask(Runnable runnable) {
        service.execute(runnable);
    }

    private List<Item> getTopArtists() {
        List<Item> result = new ArrayList<Item> ();
        JsonParser parser = new JsonParser();
        
        try {
            URL lastfm = new URL("http://ws.audioscrobbler.com/2.0/?method=chart.getTopArtists&api_key=332c084e0c3fe0f2cf6a913d21d84404&limit=1000&format=json");//Properies.getProperty(""));
            JsonElement retrieved;

            BufferedReader in = new BufferedReader(new InputStreamReader(lastfm.openStream()));
            retrieved = parser.parse(in);
            if(retrieved.isJsonObject()) {
                JsonObject jsonObject = retrieved.getAsJsonObject();
                if(jsonObject.has("artists")) {
                    JsonObject jsonArtists = jsonObject.get("artists").getAsJsonObject();
                    if(jsonArtists.has("artist")) {
                        JsonArray artists = jsonArtists.get("artist").getAsJsonArray();
                        for(JsonElement artist : artists) {
                            result.add(new Item(artist.getAsJsonObject().get("name").getAsString()
                                               ,artist.getAsJsonObject().get("mbid").getAsString()));
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Map<String, String> getTopArtistsAlbums(Item artist) {
        Map <String, String> result = new HashMap<String, String>();
        JsonParser parser = new JsonParser();

        try {
            URL lastfm = new URL("http://ws.audioscrobbler.com/2.0/?method=artist.getTopAlbums&api_key=332c084e0c3fe0f2cf6a913d21d84404&limit=1000&format=json&artist=" + artist.getName());//Properies.getProperty(""));
            JsonElement retrieved;

            BufferedReader in = new BufferedReader(new InputStreamReader(lastfm.openStream()));
            retrieved = parser.parse(in);
            //JsonArray albums = retrieved.getAsJsonObject().get("topalbums").getAsJsonObject().get("album").getAsJsonArray();
            if(retrieved.isJsonObject()) {
                JsonObject jsonObject = retrieved.getAsJsonObject();
                if(jsonObject.has("topalbums")) {
                    JsonObject jsonTopAlbums = jsonObject.get("topalbums").getAsJsonObject();
                    if( jsonTopAlbums .has("album")) {
                        JsonElement jsonAlbum = jsonTopAlbums.get("album");
                        if(jsonAlbum.isJsonArray()) {
                            JsonArray albums =  jsonAlbum.getAsJsonArray();
                            for(JsonElement album : albums) {
                                String retrievedArtistName = album.getAsJsonObject().get("artist").getAsJsonObject().get("name").getAsString();
                                if(retrievedArtistName.equals(artist.getName())) {
                                    result.put(album.getAsJsonObject().get("mbid").getAsString()
                                            , album.getAsJsonObject().get("name").getAsString());
                                }
                            }
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
