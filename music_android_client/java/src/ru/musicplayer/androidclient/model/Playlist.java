package ru.musicplayer.androidclient.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Playlist extends Model implements Comparable<Playlist> {
    private LinkedList<Track> myData = new LinkedList<Track>();
    private int nowPlaying = -1;

    public Playlist (String name) {
       // super(name);
        this.name = name;
    }
    
//    public Playlist (List<PlaylistResultEntry> entries) {
//        wasFilled = false;
//    name!!!
//        Collections.sort(entries);
//        for (PlaylistResultEntry entry: entries) {
//            myData.add(new Track(entry));
//        }
//    }
    
    public Playlist (String name, boolean filled) {
        wasFilled = filled;
        this.name = name;
    }

    @Override
    public void fillWith(Model playlist) {
        super.fillWith(playlist);
        nowPlaying = -1;
        if (myData.isEmpty()) {
            myData = ((Playlist) playlist).getMyData();
            return;
        }
        LinkedList<Track> data = ((Playlist) playlist).getMyData();
        data.addAll(myData);
        myData = data;
    }

    public void addTracks (List<Track> tracks) {
        for (Track track: tracks) {
            add(track);
        }
    }

    //this method is only for loading from authorization server to avoid millions of checks
    public void loadTrack (Track track) {
        myData.push(track);
    }
    
    public boolean add (Track track) {
        if (myData.contains(track))
            return false;
        myData.push(track);
        return true;
    }

    public void addAndPlay (Track track) {
        if (add(track))
            nowPlaying = myData.size()-1;
        else
            nowPlaying = myData.indexOf(track);
    }

    private LinkedList<Track> getMyData() {
        return myData;
    }

    @Override
    public Model[] getContent() {
        return myData.isEmpty() ?
                new Model[] {new EmptyResult("Empty playlist.")}
                : myData.toArray(new Track[myData.size()]);
    }

    public void setPlaying (String mbid) {
        for (int i=0; i<myData.size(); ++i) {
            if (myData.get(i).getMbid().equals(mbid)) {
                nowPlaying = i;
                return;
            }
        }
        throw new RuntimeException("Track " + mbid + " is not in " + name + " playlist!");
    }

    public Track getPlayingTrack() {
        if (nowPlaying == -1)
            return null;
        return myData.get(nowPlaying);
    }

    @Override
    public int compareTo(Playlist playlist) {
        return name.compareTo(playlist.name);
    }

    public boolean isPlaying() {
        return (nowPlaying != -1);
    }

    public boolean isAtTheEnd() {
        return nowPlaying == myData.size()-1;
    }

    public boolean isAtTheBeginning() {
        return nowPlaying == 0;
    }

    public void inc() {
        nowPlaying++;
    }

    public void dec() {
        nowPlaying--;
    }
    
    public Track get (int index) {
        return myData.get(index);
    }
    
    public Track start() {
        nowPlaying = 0;
        if (myData.isEmpty())
            return null;
        return myData.get(0);
    }
    
    public List<PlaylistResultEntry> toEntryList() {
        ArrayList<PlaylistResultEntry> list = new ArrayList<PlaylistResultEntry>();
        for (int i=0; i<myData.size(); ++i) {
            list.add(new PlaylistResultEntry(myData.get(i).getMbid(), i, myData.get(i).getName()));
        }
        return list;
    }
}
