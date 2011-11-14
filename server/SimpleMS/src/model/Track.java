package model;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = getValidUrl(url);
    }

    private String getValidUrl(String url) {
        return url;
        //send HEAD request
        //if(200) =>
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }
}
