package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/2/11
 * Time: 2:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class Playlist {

    public static class Entry {
        public String mbid;
        public String data;
        public int order;

        public Entry(final String id, final String data, int order) {
            this.mbid = id;
            this.data = data;
            this.order = order;
        }

        public Entry() {

        }
    }

    private String name;
    private List<Entry> entries;

    public Playlist(String name) {
        this.name = name;
        this.entries = new ArrayList<Entry>();
    }

    public Playlist (List<Entry> entries) {
        this.entries = entries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public void add (int order, final String id, final String track) {
        entries.add(new Entry(id, track, order));
    }

    private void add (Entry track) {
         entries.add(track);
    }
}
