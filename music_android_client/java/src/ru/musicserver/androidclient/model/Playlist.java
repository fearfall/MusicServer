package ru.musicserver.androidclient.model;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Playlist implements Comparable<Playlist> {
    private String myName;
    private LinkedList<Track> myData;
    private int myMaxCapacity;
    private int nowPlaying;

    public Playlist (String name, int maxCapacity) {
        myMaxCapacity = maxCapacity;
        myData = new LinkedList<Track>();
        nowPlaying = -1;
        myName = name;
    }

    public boolean add (Track track) {
        if (myData.contains(track))
            return false;
        if (myData.size() == myMaxCapacity) {
            myData.removeLast();
        }
        myData.push(track);
        return true;
    }

    public void addAndPlay (Track track) {
        if (add(track))
            nowPlaying = myData.size()-1;
        else
            nowPlaying = myData.indexOf(track);
    }

    public Model[] getData() {
        return myData.isEmpty() ?
                new Model[] {new EmptyResult("Empty playlist.")}
                : myData.toArray(new Track[myData.size()]);
    }

    public ModelContainer toModelContainer() {
        return new ModelContainer(myName, getData());
    }

    public void setPlaying (String mbid) {
        for (int i=0; i<myData.size(); ++i) {
            if (myData.get(i).getMbid().equals(mbid)) {
                nowPlaying = i;
                return;
            }
        }
        throw new RuntimeException("Track " + mbid + " is not in " + myName + " playlist!");
    }

    public Track getPlayingTrack() {
        if (nowPlaying == -1)
            return null;
        return myData.get(nowPlaying);
    }

    @Override
    public int compareTo(Playlist playlist) {
        return myName.compareTo(playlist.myName);
    }

    public Track next() {
        if (nowPlaying == -1 || nowPlaying == myData.size()-1)
            return null;
        nowPlaying++;
        return myData.get(nowPlaying);
    }

    public Track back() {
        if (nowPlaying == -1 || nowPlaying == 0)
            return null;
        nowPlaying--;
        return myData.get(nowPlaying);
    }
}
