package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/2/11
 * Time: 2:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class User {
    private String name;
    private List<Playlist> playlists;

    public User(String name) {
        this.name = name;
        playlists = new ArrayList<Playlist>();
    }


}
