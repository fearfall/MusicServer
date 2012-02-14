package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.*;
import processor.DBGrabber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 2/2/12
 * Time: 7:19 AM
 */
public class Track {
    private String name;
    private String mbid;
    private String artistMbid;
    private String albumMbid;
    private String query;
    private String artistName;
    private String albumName; 
    private Set<String> puids;
    private List<TrackInfo> infos = new LinkedList<TrackInfo>();
    private int length;
    private long start;
    private long end;
    private static Logger LOG = Logger.getLogger(Track.class);

    public Track(String mbid, String name, String artistMbid, String albumMbid,
                 String artistName, Set<String> puids, String query, int length) {
        this.name = name;
        this.mbid = mbid;
        this.artistMbid = artistMbid;
        this.albumMbid = albumMbid;
        this.artistName = artistName;
        this.puids = puids;
        this.query = query;
        this.length = length;
        ConsoleAppender trackConsoleAppender = new ConsoleAppender(new PatternLayout("%d [%t] %-5p %c %x - %m%n"));
        trackConsoleAppender.setName("trackConsoleAppender");
        if(LOG.getAppender("trackConsoleAppender") == null)
            LOG.addAppender(trackConsoleAppender);

    }

    public void setStart(long start) {
        this.start = start;
    }

    public boolean loadTrackInfo(String uid, String accessToken) {
        LOG.info("LOADING TRACK INFO FOR QUERY" + query);
        List<Integer> lengths = new LinkedList<Integer>();
        JsonParser parser = new JsonParser();
        String url = "q=" + StringEscapeUtils.escapeHtml4(query)
                + "&uid=" + uid
                + "&access_token=" + accessToken;
        JsonElement retrieved;
        try {
           // System.setProperty("https.proxyHost", "192.168.0.2");
           // System.setProperty("https.proxyPort", "3128");
            String resUrl = new URI("https","api.vk.com", "/method/audio.search", url, null).toASCIIString();
            URL vkSearch = new URL(resUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(vkSearch.openStream()));
            retrieved = parser.parse(in);
            if(retrieved.getAsJsonObject().has("response")) {
                JsonArray songs = retrieved.getAsJsonObject().get("response").getAsJsonArray();
                for(JsonElement element : songs) {
                    if(!element.isJsonPrimitive()) {
                        JsonObject jsonObject = element.getAsJsonObject();
                        int duration = jsonObject.get("duration").getAsInt();
                        if(Math.abs(((double)length)/1000 - duration) < 2 && !lengths.contains(duration)){
                            infos.add(new TrackInfo(jsonObject.get("url").getAsString()
                                , jsonObject.get("owner_id").getAsString() + "_" + jsonObject.get("aid").getAsString()
                                , duration, length));
                            lengths.add(duration);
                        }
                    } else {
                        if(element.getAsInt() == 0) {
                            LOG.error("WHILE LOADING TI ERROR FOR TRACK " + name + " RESPONSE[0]");
                            DBGrabber.getInstance().saveError(this, "query", ResultType.E_NO_INFO);
                        }
                    }
                }
            } else {
                LOG.error("WHILE LOADING TI ERROR FOR TRACK " + name + " NO RESPONSE TAG WAS FOUND");
            }
        } catch (JsonSyntaxException e) {
            LOG.error("WHILE LOADING TI ERROR FOR TRACK " + name + " JSON SYNTAX ERROR OCCURRED [" + url + "]");
            //e.printStackTrace();
        } catch (MalformedURLException e) {
            LOG.error("WHILE LOADING TI ERROR FOR TRACK " + name + " MALFORMED URI ERROR OCCURRED [" + url + "]");
            //e.printStackTrace();
        } catch (IOException e) {
            LOG.error("WHILE LOADING TI ERROR FOR TRACK " + name + " IO ERROR OCCURRED [" + url + "]");
            //e.printStackTrace();
        } catch (URISyntaxException e) {
            LOG.error("WHILE LOADING TI ERROR FOR TRACK " + name + " URI SYNTAX ERROR OCCURRED [" + url + "]");
            //e.printStackTrace();
        }
        if(infos.isEmpty()) {
            LOG.error("WHILE LOADING TI ERROR FOR TRACK " + name + " EMPTY LIST OF INFOS ERROR OCCURRED");
            DBGrabber.getInstance().saveError(this, "No track", ResultType.E_NO_TRACK);

        } else {
            LOG.info("LOADING TI FOR TRACK " + name + " FINISHED SUCCESSFULLY");
        }
        return !infos.isEmpty();
    }

    public void saveResult() {
        LOG.info("SAVING RESULTS FOR TRACK " + name);
        List<TrackInfo> toRemove = new ArrayList<TrackInfo>();
        for(TrackInfo info : infos) {
            info.retrieveUrl();
            if(!info.loadPUID()) {
                toRemove.add(info);
            } else {
                String puid = info.getPuid();
                if(puids.contains(puid)) {
                    DBGrabber.getInstance().saveTrack(this, info, false);
                    return;
                }
            }
        }
        infos.removeAll(toRemove);
        if(!infos.isEmpty()) {
            DBGrabber.getInstance().saveTrack(this, infos.get(0), true);
        } else {
            LOG.error("WHILE SAVING TRACK RESULTS ERROR FOR TRACK " + name + " NO PUIDS FOR TRACK ERROR OCCURRED");
            DBGrabber.getInstance().saveError(this, "No puid", ResultType.E_NO_PUID);
        }
    }

    public String getMbid() {
        return mbid;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return System.currentTimeMillis() - start;
    }

    boolean checkArtist(TrackInfo info){
        return DBGrabber.getInstance().getArtistMbids(info.getPuid()).contains(artistMbid);
    }

    public String getAlbumMbid() {
        return albumMbid;
    }

    public String getAlbumName() {
        return albumName;
    }
}
