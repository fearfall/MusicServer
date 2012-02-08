package ru.musicplayer.androidclient.model;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 05/02/12
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistResultEntry implements Comparable<PlaylistResultEntry> {
    private String myMbid = null;
    private int myOrder = -1;

    public PlaylistResultEntry (String mbid, int order) {
        myMbid = mbid;
        myOrder = order;
    }

    public String getMbid () {
        return myMbid;
    }

    @Override
    public int compareTo(PlaylistResultEntry playlistResultEntry) {
        if (myOrder > playlistResultEntry.myOrder)
            return 1;
        if (myOrder == playlistResultEntry.myOrder)
            return 0;
        return -1;
    }
}
