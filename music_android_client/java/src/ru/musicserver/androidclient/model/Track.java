package ru.musicserver.androidclient.model;


/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/18/11
 * Time: 4:04 PM
 */
public class Track extends Model {
    //private String name;
    //private String mbid;
    private String url;

    public Track(String name, String url, String mbid) {
        this.name = name;
        this.url = url;
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public Track[] getContent() {
        return null;
    }
}
