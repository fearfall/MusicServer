package model;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

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
<<<<<<< HEAD
        /*this.url = url;*/

=======
        this.url = url;
        //setUrl(url);
>>>>>>> ac1c7676992acafc5ccb5d245e145495251a0977
        this.mbid = mbid;
        setUrl(url);
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
        HttpURLConnection connection =
        (HttpURLConnection) new URL(url).openConnection();
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
}
