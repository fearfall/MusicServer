package ru.musicserver.androidclient.model;

import java.sql.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Playlist {
    private ArrayDeque<Track> myData;
    private int myMaxCapacity;
    private int nowPlaying;

    public Playlist (int maxCapacity) {
        myMaxCapacity = maxCapacity;
        myData = new ArrayDeque<Track>();
        nowPlaying = -1;
    }

    public void add (Track track) {
        if (myData.size() == myMaxCapacity) {
            myData.removeLast();
        }
        myData.push(track);
    }

    public ArrayDeque<Track> getData() {
        return myData;
    }

}
