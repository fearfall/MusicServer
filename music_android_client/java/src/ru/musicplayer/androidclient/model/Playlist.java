package ru.musicplayer.androidclient.model;

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

    @Override
    public void fillWith(Model playlist) {
        super.fillWith(playlist);
        myData = ((Playlist)playlist).getMyData();
        nowPlaying = -1;
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

   /* public ModelContainer toModelContainer() {
        return new ModelContainer(myName, getData());
    }   */

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
}
