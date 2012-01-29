package finder.model;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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
        //setUrl(url);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = getValid(url);
    }

    private String getValid(String url) {
        return (exists(url)) ? url : getUrlFromSource();
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public static boolean exists(String url){
    try {
      HttpURLConnection.setFollowRedirects(false);
      HttpURLConnection connection =
         (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("HEAD");
      return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
    }
    catch (Exception e) {
       e.printStackTrace();
       return false;
    }
  }

    public String getUrlFromSource() {
        /*ProcessBuilder processBuilder  = new ProcessBuilder("python url_update.py", url);
        try {
            Process p = processBuilder.start();
            Scanner s = new Scanner(p.getInputStream());
            return s.nextLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
        try {
            url = "http://localhost:6007/up/" + mbid;
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                return new Scanner(connection.getInputStream()).nextLine();
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean isValid() {
        return !(name == null || name.isEmpty()
               || mbid.isEmpty() || mbid == null
                || url == null || url.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (mbid != null ? !mbid.equals(track.mbid) : track.mbid != null) return false;
        if (name != null ? !name.equals(track.name) : track.name != null) return false;
        if (url != null ? !url.equals(track.url) : track.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (mbid != null ? mbid.hashCode() : 0);
        return result;
    }
}
