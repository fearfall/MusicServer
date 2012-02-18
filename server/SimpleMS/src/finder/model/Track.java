package finder.model;

import com.google.gson.*;
import finder.handlers.MusicServerException;
import finder.handlers.SimpleDBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/18/11
 * Time: 4:04 PM
 */
public class Track {
    private String name = "";
    private String url = "";
    private String mbid = "";

    public Track() {}

    public Track(String name, String url, String mbid) {
        this.name = name;
        this.url = url;
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String getValid(String url, String aid, String uid, String accessToken) throws MusicServerException {
        return (exists(url)) ? url : getUrlFromSource(aid, uid, accessToken);
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public static boolean exists(String url) throws MusicServerException {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            throw new MusicServerException(e.getMessage());
        }
    }

    public String getUrlFromSource(String aid, String uid, String accessToken) throws MusicServerException {
        JsonParser parser = new JsonParser();
        String url = "audios=" + aid
                + "&uid=" + uid
                + "&access_token=" + accessToken;
        JsonElement retrieved;
        try {
            //System.setProperty("https.proxyHost", "192.168.0.2");
            //System.setProperty("https.proxyPort", "3128");
            String resUrl = new URI("https","api.vk.com", "/method/audio.getById", url, null).toASCIIString();
            URL vkSearch = new URL(resUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(vkSearch.openStream()));
            retrieved = parser.parse(in);
            if(retrieved.getAsJsonObject().has("response")) {
                JsonArray songs = retrieved.getAsJsonObject().get("response").getAsJsonArray();
                for(JsonElement element : songs) {
                    if(!element.isJsonPrimitive()) {
                        final String newUrl = element.getAsJsonObject().get("url").getAsString();
                        new Thread() {
                            public void run() {
                                try {
                                    SimpleDBConnection.getInstance().saveUrl(mbid, newUrl);
                                } catch (MusicServerException e) {}
                            }
                        }.start();
                        return newUrl;
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            throw new MusicServerException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new MusicServerException(e.getMessage());
        } catch (IOException e) {
            throw new MusicServerException(e.getMessage());
        } catch (URISyntaxException e) {
            throw new MusicServerException(e.getMessage());
        }
        return null;
    }

    public boolean isValid() {
        return !(name == null || name.isEmpty()
                || mbid.isEmpty() || mbid == null
                || url == null || url.isEmpty());
    }

    public void checkUrl(String url, String aid, String uid, String accessToken) throws MusicServerException {
        this.url = getValid(url, aid, uid, accessToken);
    }
}
